WAREHOUSE MANAGEMENT SYSTEM  
============================

Description:
------------
This is a backend Spring Boot project for managing a warehouse system.

It handles:
- User management (with roles: SYSTEM_ADMIN, WAREHOUSE_MANAGER, CLIENT)
- Item and inventory management
- Order management (create, approve, decline, cancel, submit)
- Truck scheduling and delivery management

Main Features:
--------------
✅ User registration and admin updates  
✅ Secure authentication using JWT  
✅ Role-based access control  
✅ CRUD operations for Items and Inventory  
✅ Orders with approval and delivery flow  
✅ Trucks assigned to deliveries, no duplicate trucks on the same date  
✅ Automatic stock reduction when an order is approved  

Tech Stack:
-----------
- Java 17  
- Spring Boot 3.x  
- Spring Security + JWT  
- Spring Data JPA  
- MySQL  
- Lombok  
- Swagger (for API documentation)

How to Run:
-----------
1. Make sure you have MySQL running and configured in `application.properties`:
   - DB name: warehouse_db
   - Username & password (default: root/root)

2. Build the project:
mvn clean install

3. Run the project:
mvn spring-boot:run

4. Access API:
http://localhost:8080/

5. Access Swagger UI:
http://localhost:8080/swagger-ui.html

Postman Collection:
-------------------
✅ Auth (login, signup)  
✅ Item CRUD  
✅ Inventory CRUD  
✅ Order flow (create, update, submit, approve, decline)  
✅ Delivery scheduling (by warehouse manager)  
✅ Truck management  
✅ Admin user updates  

Database Reset:
---------------
If you need to reset the database:
- Drop the `warehouse_db` schema manually from MySQL
- Run the app again → Hibernate will recreate tables

Important Notes:
----------------
- Only one delivery per truck per day allowed  
- Only approved orders can be scheduled for delivery  
- Stock quantities decrease when an order is approved  

Author:
-------
Erison Minaj
erison.minaj54@gmail.com




