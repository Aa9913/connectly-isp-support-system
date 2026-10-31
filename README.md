# Connectly — ISP Customer Support and Connectivity Issue Resolution System

Connectly is a web application for Internet Service Provider (ISP) customers to report connectivity and account-related issues without needing to contact support by phone for every complaint. Customers can submit a complain using a ticket, follow its progress, and read resolution updates. Support staff can review, assign, and resolve tickets through role-specific dashboards.


## Main Features

- **Customer accounts:** Registration, login, complaint submission, and ticket tracking.
- **Ticket management:** Create, view, edit eligible open tickets, search, filter, and safely delete tickets through an authorized administrator.
- **Complaint details:** Customer name, contact number, affected area, optional address or landmark, issue category, subject, and description.
- **Level 1 support:** Review incoming complaints, resolve simple cases, or manually assign them to specialist staff.
- **Specialist support:** Technical Officers, Field Technicians, and Billing Support staff can access their assigned tickets and update their progress.
- **Progress tracking:** Ticket statuses, staff notes, estimated resolution date/time, and ticket history.
- **Notifications:** In-application notifications for relevant ticket updates.
- **Dashboards:** Role-specific views and support statistics.

## Technologies Used

| Component | Technology |
| --- | --- |
| Backend | Java 21, Spring Boot, Spring MVC |
| Frontend | HTML, CSS, JavaScript, Thymeleaf |
| Security | Spring Security, BCrypt password hashing |
| Database | MongoDB |
| Data access | Spring Data MongoDB |
| Build tool | Apache Maven |
| Development environment | IntelliJ IDEA |

## User Roles

| Role | Main responsibilities |
| --- | --- |
| Customer | Create complaints and monitor their status and resolution |
| Level 1 Support | Review complaints, record solutions, or assign them to specialists |
| Technical Officer | Work on assigned technical complaints |
| Field Technician | Handle assigned on-site or equipment-related complaints |
| Billing Support | Handle assigned billing and account complaints |
| Administrator | Manage staff accounts, view records, and remove tickets when necessary |

## Ticket Workflow

1. A customer signs in and submits a complaint with contact and location details.
2. The ticket is created with an **Open** status.
3. Level 1 Support reviews the complaint.
4. The agent either resolves the issue or assigns the ticket to a suitable specialist with a note and estimated resolution time.
5. The assigned specialist updates the ticket while working and marks it **Resolved** when finished.
6. The customer can view ticket updates and notifications throughout the process.

Ticket statuses: `OPEN`, `UNDER_REVIEW`, `PENDING`, `IN_PROGRESS`, and `RESOLVED`.

## Database Design

The MongoDB database is named `isp_support` and uses six collections:

| Collection | Purpose |
| --- | --- |
| `customer` | Customer account and contact information |
| `service_provider` | Level 1 and specialist staff accounts and roles |
| `admin` | Administrator accounts |
| `tickets` | Complaint details, customer and assignment references, status, and resolution details |
| `notification` | In-app notifications |
| `ticket_updates` | Status changes and notes forming the ticket timeline |

The `tickets` collection stores references to the customer and assigned provider. Ticket updates and notifications refer to their associated tickets. These are application-managed references, not relational foreign-key constraints.


## Installation and Running the Application

1. Clone the GitHub repository or download and extract the project ZIP.
2. Open the **`isp-support-system`** folder (the folder containing `pom.xml`) in IntelliJ IDEA.
3. Set the Project SDK to **JDK 21** and allow Maven to load the dependencies.
4. Start MongoDB. For a local installation, MongoDB should be available at `mongodb://localhost:27017`.
5. Open a terminal in the project root and run:

   ```powershell
   mvn clean test
   mvn spring-boot:run
   ```

6. Open the website in a browser at **http://localhost:8080/**.

### Main Pages

| URL | Page |
| --- | --- |
| `/` or `/welcome` | Public Connectly welcome page |
| `/login` | Sign-in page |
| `/register` | Customer registration |
| `/dashboard` | Dashboard based on the signed-in user's role |
| `/tickets` | Ticket listing (subject to access permissions) |
| `/notifications` | In-app notifications |

## Application Configuration

The main configuration file is `src/main/resources/application.properties`.

```properties
server.port=${PORT:8080}
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/isp_support}
app.demo-seed=${APP_DEMO_SEED:true}
```

- Change `PORT` if port 8080 is already in use.
- Set `MONGODB_URI` when using a database other than the local default.
- The project supports **local demo seeding** to create sample accounts and a sample ticket. The login details are defined in `DemoDataSeeder.java`; use these only for local testing.
- **Before public deployment**, disable demo seeding (`APP_DEMO_SEED=false`) and remove or secure any existing demo accounts. Disabling the seeder does not delete previously created accounts.
- Do not commit real passwords, database credentials, or `.env` files to GitHub.

## Basic Testing Procedure

1. Register or sign in as a customer and create a complaint.
2. Open the new complaint and verify its details and status.
3. Sign in as Level 1 Support and review the complaint.
4. Assign it to an appropriate staff member with a note and estimated completion date, or resolve it directly.
5. Sign in as the assigned staff member, add a progress update, and mark the ticket resolved.
6. Return to the customer account and check the ticket history and notifications.
7. Use an administrator account to review staff management and safe ticket deletion.
8. Check `isp_support` in MongoDB Compass to confirm that records are stored persistently.

## Project Structure

```text
isp-support-system/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/isp/support/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── form/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── templates/
│   │       ├── static/css/
│   │       ├── static/js/
│   │       ├── static/images/
│   │       └── application.properties
│   └── test/
└── README.md
```

## Notes

Connectly is an academic project designed for local development and demonstration. Before using it as a public ISP support service, its security, account provisioning, privacy handling, and deployment configuration should be reviewed and strengthened.
