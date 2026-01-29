# Payment Service

A microservice responsible for handling payment processing in the e-commerce system. It supports multiple payment
gateways including Stripe and Razorpay.

## Features

- Payment initiation
- Multiple payment gateway support (Stripe, Razorpay)
- Payment receipt generation
- Kafka integration for event handling
- Database persistence for payment records

## Tech Stack

- Java 17
- Spring Boot 3.5.3
- Spring Data JPA
- Spring Kafka
- MySQL
- Lombok
- Stripe SDK
- Razorpay SDK

## Database Schema

### Payments Table

- receiptNumber (String, Primary Key)
- transactionId (String)
- orderId (String)
- gateway (String)
- gatewayPaymentId (String)
- amountMinor (Long)
- currency (String)
- status (Enum)
- paidAt (Timestamp)

## API Endpoints

### Initiate Payment

- **POST** `/payments`
- **Request Body:** PaymentRequest
    - orderId (String)
    - amount (Double)
    - currency (String)
    - gateway (String)
- **Response:** PaymentResponse

### Get Payment Receipt

- **GET** `/payments/{orderId}/receipt`
- **Response:** PaymentReceiptResponse

## Configuration

The service requires the following configurations in application.properties:

- Database configuration
- Kafka broker settings
- Stripe API credentials
- Razorpay API credentials

## Setup and Installation

1. Clone the repository
2. Configure application.properties with your:
    - Database settings
    - Kafka broker address
    - Payment gateway credentials
3. Build the project: `mvn clean install`
4. Run the application: `mvn spring-boot:run`
