## Endpoints

### 1. Register user

- **Description**: Creates new user
- **URL**: `/users`
- **Method**: `POST`
- **Constraints**:
    - **email**:
      + not null 
      + match with valid email regex
    - **firstName**: 
      + must be not null or blank
    - **lastName**:
      + must be not null or blank
    - **birthDate**: 
       + must be not null 
       + age must be now lower than configured one in application.yml 
- **Request Body (application/json)**:
  ```json
  {
    "data" : {
      "email" : "username@domain.com",
      "firstName" : "John",
      "lastName" : "Smith",
      "birthDate" : "1900-01-29",
      "address" : "123 Main Street, New York, NY 10001, USA",
      "phone" : "(555) 555-5555"
    }
  }
  ```

- **Response**:
  - Status: 201 CREATED
  - Header: Location=/users/100
  ```json
  {
    "data" : {
      "id" : 100,
      "email" : "username@domain.com",
      "firstName" : "John",
      "lastName" : "Smith",
      "birthDate" : "1900-01-29",
      "address" : "123 Main Street, New York, NY 10001, USA",
      "phone" : "(555) 555-5555"
    }
  }
  ```
    - Status: 4XX CLIENT ERROR
  ```json
  {
    "timestamp":"2024-05-01T21:16:49.545747",
    "status":400,
    "reason":"Bad request, missing or invalid request arguments",
    "details": {
      "data.birthDate" : "Birth date must be present"
    },
    "path":"/users"}
  ```  

### 2. Update user

- **Description**: Updates all user properties
- **URL**: `/users/{userId}`
- **Method**: `PUT`
- **Constraints**:
    - **email**:
        + not null
        + match with valid email regex
    - **firstName**:
        + must be not null or blank
    - **lastName**:
        + must be not null or blank
    - **birthDate**:
        + must be not null
        + age must be now lower than configured one in application.yml
- **Path variable**:
    - userId: user id long value
- **Request Body (application/json)**:
  ```json
  {
    "data" : {
      "email" : "username@domain.com",
      "firstName" : "John",
      "lastName" : "Smith",
      "birthDate" : "1900-01-29",
      "address" : "123 Main Street, New York, NY 10001, USA",
      "phone" : "(555) 555-5555"
    }
  }
  ```

- **Response**:
    - Status: 200 OK
  ```json
  {
    "data" : {
      "id" : 100,
      "email" : "username@domain.com",
      "firstName" : "John",
      "lastName" : "Smith",
      "birthDate" : "1900-01-29",
      "address" : "123 Main Street, New York, NY 10001, USA",
      "phone" : "(555) 555-5555"
    }
  }
  ```
    - Status: 4XX CLIENT ERROR
  ```json
  {
    "timestamp":"2024-05-01T21:16:49.545747",
    "status":400,
    "reason":"Bad request, missing or invalid request arguments",
    "details": {
      "data.birthDate" : "Birth date must be present"
    },
    "path":"/users/100"}
  ```  

### 3. Update user contacts

- **Description**: Updates user properties received from request body (only present parameters)
- **URL**: `/users/{userId}`
- **Method**: `PATCH`
- **Constraints**:
    - **email**: 
       + must match with valid email regex
- **Path variable**:
    - userId: user id long value
- **Request Body (application/json)**:
  ```json
  {
    "data" : {
      "email" : "username@domain.com",
      "address" : "123 Main Street, New York, NY 10001, USA",
      "phone" : "(555) 555-5555"
    }
  }
  ```

- **Response**:
    - Status: 200 OK
  ```json
  {
    "data" : {
      "id" : 100,
      "email" : "username@domain.com",
      "firstName" : "John",
      "lastName" : "Smith",
      "birthDate" : "1900-01-29",
      "address" : "123 Main Street, New York, NY 10001, USA",
      "phone" : "(555) 555-5555"
    }
  }
  ```
    - Status: 4XX CLIENT ERROR
  ```json
  {
    "timestamp":"2024-05-01T21:16:49.545747",
    "status":400,
    "reason":"Bad request, missing or invalid request arguments",
    "details": {
      "data.email" : "Invalid email regex"
    },
    "path":"/users/100"}
  ```  

### 4. Delete user

- **Description**: Delete user
- **URL**: `/users/{userId}`
- **Method**: `DELETE`
- **Path variable**:
    - userId: user id long value
- **Response**:
    - Status: 200 OK
    - Status: 4XX CLIENT ERROR
  ```json
  {
    "timestamp":"2024-05-01T21:16:49.7977445",
    "status":404,
    "reason":"User not found",
    "path":"/users/100"
  }
  ```  

### 5. Find users by birthdate range

- **Description**: Find users by birthdate range
- **URL**: `/users`
- **Method**: `GET`
- **Request Parameters**:
    - **from**:
        + required
        + pattern 'yyyy-mm-dd'
  - **from**:
      + required
      + pattern 'yyyy-mm-dd'
      + value must be equal or greater than 'from' value
- **Response**:
    - Status: 200 OK
  ```json
  {
    "data": [
      {
        "id":20,
        "email":"email4@gmail.com",
        "firstName":"name4",
        "lastName":"last4",
        "birthDate":"2024-04-30",
        "address":"address4",
        "phone":"phone4"
      },
      {
        "id":21,
        "email":"email5@gmail.com",
        "firstName":"name5",
        "lastName":"last5",
        "birthDate":"2024-05-01",
        "address":"address5",
        "phone":"phone5"
      }
    ]
  }
  ```
    - Status: 4XX CLIENT ERROR
  ```json
  {
    "timestamp":"2024-05-01T21:16:49.7307452",
    "status":400,
    "reason":"Bad request, missing or invalid request arguments",
    "details": {
      "to, from" : "To must be greater or equals from."
    },
    "path":"/users"
  }
  ```  