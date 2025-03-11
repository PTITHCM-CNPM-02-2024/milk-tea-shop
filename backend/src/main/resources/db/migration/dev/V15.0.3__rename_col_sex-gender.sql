ALTER TABLE Customer
    CHANGE sex gender ENUM('MALE', 'FEMALE', 'OTHER');

ALTER TABLE Employee
    CHANGE sex gender ENUM('MALE', 'FEMALE', 'OTHER');

ALTER TABLE Manager
    CHANGE sex gender ENUM('MALE', 'FEMALE', 'OTHER');