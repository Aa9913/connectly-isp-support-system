package com.isp.support.service;

import com.isp.support.form.TicketForm;
import com.isp.support.model.*;
import com.isp.support.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceTest {
    TicketRepository tickets = mock(TicketRepository.class);
    TicketUpdateRepository updates = mock(TicketUpdateRepository.class);
    NotificationRepository notes = mock(NotificationRepository.class);
    NotificationService notificationService = mock(NotificationService.class);
    ServiceProviderRepository providers = mock(ServiceProviderRepository.class);
    TicketService service;
    @BeforeEach void setup() {
        reset(tickets, updates, notes, notificationService, providers);
        service = new TicketService(tickets, updates, notes, notificationService, providers);
        when(tickets.save(any(Ticket.class))).thenAnswer(i -> {
            Ticket t = i.getArgument(0);
            if (t.getId() == null) t.setId("ticket-1");
            return t;
        });
        when(providers.findByRole(Role.LEVEL1)).thenReturn(List.of());
    }

    @Test void customerCanCreatePersistentTicketWithHistory() {
        var me = new CurrentUserService.Identity("customer-1", "Ada", "ada@example.com", Role.CUSTOMER);
        var f = new TicketForm();
        f.setCustomerName("Ada Perera");
        f.setCustomerPhone("0771234567");
        f.setCustomerArea("Negombo");
        f.setLocationDetails("Near the bus stand");
        f.setCategory(Category.SLOW_SPEED);
        f.setSubject("Slow internet at night");
        f.setDescription("Speed drops drastically every night after 7 pm.");
        var ticket = service.create(me, f);
        assertEquals(TicketStatus.OPEN, ticket.getStatus());
        assertEquals("customer-1", ticket.getCustomerId());
        assertEquals("Ada Perera", ticket.getCustomerName());
        assertEquals("0771234567", ticket.getCustomerPhone());
        assertEquals("Negombo", ticket.getCustomerArea());
        assertEquals("Near the bus stand", ticket.getLocationDetails());
        assertNotNull(ticket.getTicketNumber());
        verify(tickets).save(any(Ticket.class));
        verify(updates).save(any(TicketUpdate.class));
        verify(notificationService).send(eq(RecipientType.CUSTOMER), eq("customer-1"), eq("ticket-1"), anyString(), anyString());
    }

    @Test void customerCanEditContactInformationOnOpenTicket() {
        Ticket t = new Ticket();
        t.setId("ticket-3");
        t.setCustomerId("customer-1");
        t.setCustomerName("Ada");
        t.setStatus(TicketStatus.OPEN);
        when(tickets.findById("ticket-3")).thenReturn(Optional.of(t));
        var me = new CurrentUserService.Identity("customer-1", "Ada", "ada@example.com", Role.CUSTOMER);
        TicketForm f = new TicketForm();
        f.setCustomerName("Ada Perera");
        f.setCustomerPhone("0712345678");
        f.setCustomerArea("Colombo 05");
        f.setLocationDetails("Near hospital");
        f.setCategory(Category.SLOW_SPEED);
        f.setSubject("Slow internet at night");
        f.setDescription("Speeds drop throughout the evening.");
        service.editOwn("ticket-3", me, f);
        assertEquals("Ada Perera", t.getCustomerName());
        assertEquals("0712345678", t.getCustomerPhone());
        assertEquals("Colombo 05", t.getCustomerArea());
        assertEquals("Near hospital", t.getLocationDetails());
        verify(tickets).save(t);
    }

    @Test void customerCannotViewOtherCustomersTicket() {
        Ticket t = new Ticket();
        t.setId("x");
        t.setCustomerId("owner");
        when(tickets.findById("x")).thenReturn(Optional.of(t));
        var visitor = new CurrentUserService.Identity("different", "Test", "test@example.com", Role.CUSTOMER);
        assertThrows(AccessDeniedException.class, () -> service.visible("x", visitor));
    }

    @Test void assignedTechnicianCanResolveTicket() {
        Ticket t = new Ticket();
        t.setId("ticket-2");
        t.setTicketNumber("ISP-123");
        t.setCustomerId("customer-1");
        t.setAssignedProviderId("tech-1");
        t.setStatus(TicketStatus.PENDING);
        when(tickets.findById("ticket-2")).thenReturn(Optional.of(t));
        var technician = new CurrentUserService.Identity("tech-1", "Nimal", "nimal@example.com", Role.TECHNICAL);
        service.updateAssigned("ticket-2", technician, TicketStatus.RESOLVED, "Restarted customer ONT and restored connectivity");
        assertEquals(TicketStatus.RESOLVED, t.getStatus());
        assertNotNull(t.getResolvedAt());
        verify(updates).save(any(TicketUpdate.class));
        verify(notificationService).send(eq(RecipientType.CUSTOMER), anyString(), anyString(), eq("Complaint resolved"), anyString());
    }

    @Test void nonAssignedTechnicianCannotResolve() {
        Ticket t = new Ticket();
        t.setId("ticket-2");
        t.setAssignedProviderId("tech-1");
        t.setCustomerId("customer-1");
        t.setStatus(TicketStatus.PENDING);
        when(tickets.findById("ticket-2")).thenReturn(Optional.of(t));
        var technician = new CurrentUserService.Identity("tech-2", "Other", "other@example.com", Role.TECHNICAL);
        assertThrows(AccessDeniedException.class, () -> service.updateAssigned("ticket-2", technician, TicketStatus.RESOLVED, "Changed something"));
    }
}
