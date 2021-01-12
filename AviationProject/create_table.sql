CREATE TABLE location(
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,

    CONSTRAINT pk_location PRIMARY KEY (city, state, country)
);

CREATE TABLE airport(
    name VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(10) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,

    CONSTRAINT pk_airport PRIMARY KEY (abbreviation),

    CONSTRAINT fk_airport_location
    FOREIGN KEY (city, state, country)
    REFERENCES location(city, state, country)
);

CREATE TABLE airline(
    name VARCHAR(100) NOT NULL,
    headQuarters VARCHAR(100) NOT NULL,
    type VARCHAR(100) NOT NULL,
    dateEstablished DATE NOT NULL,

    CONSTRAINT pk_airline PRIMARY KEY (name)
);

CREATE TABLE partner(
    name VARCHAR(100) NOT NULL,
    abbreviation VARCHAR(10) NOT NULL,

    CONSTRAINT pk_partner PRIMARY KEY (name, abbreviation),

    CONSTRAINT fk_partner_aiport
    FOREIGN KEY (abbreviation)
    REFERENCES airport(abbreviation),

    CONSTRAINT fk_partner_airline
    FOREIGN KEY (name)
    REFERENCES airline(name)
);

CREATE TABLE flightSchedule(
    origin VARCHAR(10) NOT NULL,
    destination VARCHAR(10) NOT NULL,
    airlineName VARCHAR(100) NOT NULL,
    departureTime TIME NOT NULL,
    arrivalTime TIME NOT NULL,
    flightId INT NOT NULL,
    
    CONSTRAINT pk_flightId PRIMARY KEY (flightId),
    
    CONSTRAINT fk_flightScheduleOrigin_Airport
    FOREIGN KEY (origin)
    REFERENCES airport(abbreviation),

    CONSTRAINT fk_flightScheduleDestination_Airport
    FOREIGN KEY (destination)
    REFERENCES airport(abbreviation),
    
    CONSTRAINT fk_flightScheduleAirlineName_Airline
    FOREIGN KEY (airlineName)
    REFERENCES airline(name)
);

CREATE TABLE airplane(
    airlineName VARCHAR(100) NOT NULL, 
    name VARCHAR(100) NOT NULL,
    manufacturer VARCHAR(100) NOT NULL,
    modelNumber INT NOT NULL,
    tailNumber VARCHAR(100) NOT NULL,
    seatingCapacity INT NOT NULL,
    
    CONSTRAINT pk_airplaneTailNumber PRIMARY KEY (tailNumber),
    
    CONSTRAINT fk_airplaneName
    	FOREIGN KEY (airlineName)
    REFERENCES airline(name)    
);

CREATE TABLE maintenanceCheck(
    leadInspector VARCHAR(100) NOT NULL,
    type VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    tailNumber VARCHAR(100) NOT NULL,
   	 
    CONSTRAINT pk_maitenanceCheck PRIMARY KEY (type, date, tailNumber),
    
    CONSTRAINT fk_maintenanceCheck
    FOREIGN KEY (tailNumber)
    REFERENCES airplane(tailNumber)
);

CREATE TABLE flightInstance(
    flightId INT NOT NULL,
    flightDate DATE NOT NULL,
    departureTime TIME NOT NULL,
    arrivalTime TIME NOT NULL,
    tailNumber VARCHAR(100) NOT NULL,
    
    CONSTRAINT pk_flightInstance PRIMARY KEY (flightId, flightDate),
    
    CONSTRAINT fk_flightInstance_flightSchedule
    FOREIGN KEY (flightId)
    REFERENCES flightSchedule(flightId),
    
    CONSTRAINT fk_flightInstance_airplane
    FOREIGN KEY (tailNumber)
    REFERENCES airplane(tailNumber)
);

CREATE TABLE crew(
    numberOfFlightAttendants INT NOT NULL,
    crewId INT NOT NULL,
    
    CONSTRAINT pk_crew
    PRIMARY KEY (crewId)
);

CREATE TABLE flightAssignment(
    crewId INT NOT NULL,
    flightId INT NOT NULL,
    
    CONSTRAINT pk_flightAssignment
    PRIMARY KEY (crewId, flightId),
    
    CONSTRAINT fk_flightAssignment_flightSchedule
    FOREIGN KEY (flightId)
    REFERENCES flightSchedule(flightId),

    CONSTRAINT fk_flightAssignment_crew
    FOREIGN KEY (crewId)
    REFERENCES crew(crewId)
);

CREATE TABLE crewMember(
    name VARCHAR(100) NOT NULL,
    type VARCHAR(100) NOT NULL,
    backGroundCheckDate DATE NOT NULL, 
    FAANumber INT NOT NULL,
    salary    INT NOT NULL,
    crewId INT NOT NULL,

    CONSTRAINT pk_crewMember
    PRIMARY KEY (FAANumber),

    CONSTRAINT fk_crewId 
    FOREIGN KEY (crewId)
    REFERENCES crew(crewId)
);

CREATE TABLE incidentReport(
    flightId INT NOT NULL,
    flightDate DATE NOT NULL,
    type VARCHAR(100) NOT NULL,
    description VARCHAR(100) NOT NULL,
    crewMemberReport INT NOT NULL,
    crewMemberInvolved INT NOT NULL, 
    
    CONSTRAINT pk_incidentReport
    PRIMARY KEY (flightId, flightDate, type, description, crewMemberReport, crewMemberInvolved),

    CONSTRAINT fk_incidentReport
    FOREIGN KEY (flightId, flightDate)
    REFERENCES flightInstance(flightId, flightDate),
    
    CONSTRAINT fk_incidentReportCrewMemberReport
    FOREIGN KEY (crewMemberReport)
    REFERENCES crewMember (FAANumber),

    CONSTRAINT fk_incidentReportCrewMemberInvolved
    FOREIGN KEY (crewMemberInvolved)
    REFERENCES crewMember (FAANumber)
);

CREATE TABLE internationalFlight(
    flightId INT NOT NULL,
    wiFi BOOLEAN NOT NULL, 
    cost INT NOT NULL, 
    
    CONSTRAINT pk_internationalFlight
    PRIMARY KEY (flightId, wiFi, cost),

    CONSTRAINT fk_international_flightSchedule
    FOREIGN KEY (flightId)
    REFERENCES flightSchedule(flightId)
);

CREATE TABLE localDomesticFlight(
    flightId INT NOT NULL,
    
    CONSTRAINT pk_localDomesticFlight
    PRIMARY KEY (flightId),
    
    CONSTRAINT fk_localDomesticFlight
    FOREIGN KEY (flightId)
    REFERENCES flightSchedule(flightId)
);

CREATE TABLE extraCharges(
    cost INT NOT NULL,
    type VARCHAR(100) NOT NULL,
    
    CONSTRAINT pk_extraCharges
    PRIMARY KEY (type)
);

CREATE TABLE amenity(
    flightId INT NOT NULL,
    type VARCHAR(100) NOT NULL,

    CONSTRAINT pk_amenity
    PRIMARY KEY(flightId, type),

    CONSTRAINT fk_amenityIdNumber_extraCharges
    FOREIGN KEY (flightId)
    REFERENCES localDomesticFlight(flightId),

    CONSTRAINT fk_amenityType_extraCharges
    FOREIGN KEY (type)
    REFERENCES extraCharges(type)
);