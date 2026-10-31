@echo off
echo Make sure MongoDB service is running and Maven + Java 21 are on PATH.
cd /d "%~dp0"
mvn spring-boot:run
pause
