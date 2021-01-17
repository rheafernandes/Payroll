
Prerequisites to run the project
- Java 8
- Maven

The Play Framework project which uses the Netty Server, 
the project specific configuration is in conf/application.conf
- Build the project : ```mvn clean install -DskipTests ```
- Run the project : ``` mvn play2:run ```
- Debug the project : ``` mvnDebug play2:run ```

 
The control starts at the Controller Classes and corresponding 
routes are under conf/routes

- BaseController -> Contains common methods required for Fetching 
Request Body from HTTP request
- StaffController -> CRUD API's specific to Staff members
- WorklogController -> CRUD API's related to Worklog and Payroll 

Ideally there would be a Health Controller, to check the health of the underlying services
and a Request Filter, that would route the request only if the service was healthy
else throws ServiceUnavailable Error

The control then goes to the Managers, which hold the main business logic
- StaffManager -> Contains logic specific to staff members
Basic validation is done here, ideally Mongo Codecs/Schema would be used to do this,
with the help of POJO's Staff and WorkLog
Sorting is done by Staff name and is handled in the SortingUtil Class

- WorklogManager -> Contains logic specific to updation of worklog 
& fetching salary of staff member for a particular time period in both
JSON & PDF format.
The worklog dates is converted to Epoch time for ease of storage and manipulation

The Manager is also injected with the Store Class.

Store Class has
- Sequence generator for ID generation, this uses a mongo collection 
with one variable that keeps updating for every staff created
- Prepares the necessary data required for storage in DB

MongoUtil contains all the Util methods required to access the Database.
MongoConnector is a singleton class that holds a connection object.

Salary Pdf is generated using the iText pdf library after generating html string 
from the Staff data.

Basic Tests are written for Managers and Utils.
The postman collection url: https://www.getpostman.com/collections/a40e61d79eaad8145e6a





