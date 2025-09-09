-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: library_management_db
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `books` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `author` varchar(255) NOT NULL,
  `available_copies` int NOT NULL,
  `category` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `isbn` varchar(255) NOT NULL,
  `publication_year` int DEFAULT NULL,
  `publisher` varchar(255) DEFAULT NULL,
  `shelf_location` varchar(255) DEFAULT NULL,
  `status` enum('ACTIVE','DAMAGED','INACTIVE','LOST') NOT NULL,
  `title` varchar(255) NOT NULL,
  `total_copies` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkibbepcitr0a3cpk3rfr7nihn` (`isbn`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `books`
--

LOCK TABLES `books` WRITE;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` VALUES (1,'Amanda Writer',3,'Fiction','2025-09-09 06:02:08.000000','An epic tale of adventure and discovery in unknown lands.','978-0-123-45678-1',2020,'Penguin Books','A1-001','ACTIVE','The Great Adventure',5,'2025-09-09 06:02:08.000000'),(2,'Robert Mystery',1,'Fiction','2025-09-09 06:02:08.000000','A thrilling mystery set in an ancient lost civilization.','978-0-234-56789-2',2019,'Random House','A1-002','ACTIVE','Mystery of the Lost City',3,'2025-09-09 06:02:08.000000'),(3,'Isabella Love',4,'Fiction','2025-09-09 06:02:08.000000','A heartwarming love story set in the romantic city of Paris.','978-0-345-67890-3',2021,'HarperCollins','A1-003','ACTIVE','Romance in Paris',4,'2025-09-09 06:02:08.000000'),(4,'Christopher Future',0,'Fiction','2025-09-09 06:02:08.000000','A science fiction adventure through time and space.','978-0-456-78901-4',2018,'Simon & Schuster','A1-004','ACTIVE','The Time Traveler',2,'2025-09-09 06:02:08.000000'),(5,'Dr. Albert Newton',6,'Science','2025-09-09 06:02:08.000000','Comprehensive guide to fundamental physics principles.','978-0-567-89012-5',2022,'Academic Press','B2-001','ACTIVE','Introduction to Physics',8,'2025-09-09 06:02:08.000000'),(6,'Prof. Marie Elements',4,'Science','2025-09-09 06:02:08.000000','Essential chemistry concepts for students and professionals.','978-0-678-90123-6',2021,'Educational Publishers','B2-002','ACTIVE','Chemistry Fundamentals',6,'2025-09-09 06:02:08.000000'),(7,'Dr. Charles Darwin Jr.',2,'Science','2025-09-09 06:02:08.000000','Exploring the wonders of life and biological systems.','978-0-789-01234-7',2020,'Scientific Books','B2-003','ACTIVE','Biology and Life Sciences',5,'2025-09-09 06:02:08.000000'),(8,'Prof. Isaac Calculator',3,'Science','2025-09-09 06:02:08.000000','Advanced mathematical concepts and applications.','978-0-890-12345-8',2019,'Math Publications','B2-004','ACTIVE','Advanced Mathematics',4,'2025-09-09 06:02:08.000000'),(9,'Tech Expert',8,'Technology','2025-09-09 06:02:08.000000','Complete guide to Java programming and development.','978-0-901-23456-9',2023,'Tech Books Ltd','C3-001','ACTIVE','Java Programming Guide',10,'2025-09-09 06:02:08.000000'),(10,'Code Master',5,'Technology','2025-09-09 06:02:08.000000','Learn HTML, CSS, and JavaScript for web development.','978-0-012-34567-0',2022,'Developer Press','C3-002','ACTIVE','Web Development Basics',7,'2025-09-09 06:02:08.000000'),(11,'Data Architect',3,'Technology','2025-09-09 06:02:08.000000','Comprehensive guide to database design and optimization.','978-0-123-45679-1',2021,'Database Books','C3-003','ACTIVE','Database Design Principles',5,'2025-09-09 06:02:08.000000'),(12,'AI Researcher',4,'Technology','2025-09-09 06:02:08.000000','Understanding AI and machine learning concepts.','978-0-234-56780-2',2023,'Future Tech','C3-004','ACTIVE','Artificial Intelligence Today',6,'2025-09-09 06:02:08.000000'),(13,'Prof. History Scholar',2,'History','2025-09-09 06:02:08.000000','Comprehensive overview of world historical events.','978-0-345-67891-3',2020,'Historical Press','D4-001','ACTIVE','World History Chronicles',4,'2025-09-09 06:02:08.000000'),(14,'Dr. Archaeology Expert',1,'History','2025-09-09 06:02:08.000000','Exploring the great civilizations of the past.','978-0-456-78902-4',2019,'Ancient Books','D4-002','ACTIVE','Ancient Civilizations',3,'2025-09-09 06:02:08.000000'),(15,'Contemporary Historian',3,'History','2025-09-09 06:02:08.000000','Analysis of significant events in modern history.','978-0-567-89013-5',2021,'Modern Press','D4-003','ACTIVE','Modern History Analysis',5,'2025-09-09 06:02:08.000000'),(16,'CEO Expert',4,'Business','2025-09-09 06:02:08.000000','Strategic planning and business development guide.','978-0-678-90124-6',2022,'Business Books','E5-001','ACTIVE','Business Strategy Guide',6,'2025-09-09 06:02:08.000000'),(17,'Finance Guru',2,'Business','2025-09-09 06:02:08.000000','Essential financial management principles for businesses.','978-0-789-01235-7',2021,'Money Publishers','E5-002','ACTIVE','Financial Management',4,'2025-09-09 06:02:08.000000'),(18,'Brand Specialist',3,'Business','2025-09-09 06:02:08.000000','Core marketing concepts and strategies.','978-0-890-12346-8',2020,'Marketing Press','E5-003','ACTIVE','Marketing Fundamentals',5,'2025-09-09 06:02:08.000000'),(19,'Life Coach',5,'Self-Help','2025-09-09 06:02:08.000000','Guide to personal growth and development.','978-0-901-23457-9',2023,'Self Improvement','F6-001','ACTIVE','Personal Development',7,'2025-09-09 06:02:08.000000'),(20,'Productivity Expert',3,'Self-Help','2025-09-09 06:02:08.000000','Effective time management techniques.','978-0-012-34568-0',2022,'Life Skills Books','F6-002','ACTIVE','Time Management Skills',4,'2025-09-09 06:02:08.000000'),(21,'Test Author',0,'Fiction','2025-09-09 06:02:08.000000','This book is marked as damaged for testing.','978-0-111-11111-1',2020,'Test Publisher','Z9-001','DAMAGED','Damaged Book Example',3,'2025-09-09 06:02:08.000000'),(22,'Another Author',0,'Science','2025-09-09 06:02:08.000000','This book is marked as lost for testing.','978-0-222-22222-2',2019,'Test Publisher','Z9-002','LOST','Lost Book Example',2,'2025-09-09 06:02:08.000000');
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `borrow_transactions`
--

DROP TABLE IF EXISTS `borrow_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrow_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `borrow_date` date NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `daily_fine_rate` decimal(5,2) DEFAULT NULL,
  `due_date` date NOT NULL,
  `fine_amount` decimal(10,2) DEFAULT NULL,
  `fine_paid` bit(1) DEFAULT NULL,
  `max_fine_amount` decimal(10,2) DEFAULT NULL,
  `max_renewals` int DEFAULT NULL,
  `notes` varchar(500) DEFAULT NULL,
  `renewal_count` int DEFAULT NULL,
  `return_date` date DEFAULT NULL,
  `status` enum('BORROWED','OVERDUE','RENEWED','RETURNED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `book_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqtcs65emvosbmm1q7n4bptwil` (`book_id`),
  KEY `FKct9jfcshu1s6oko3tt7pnw112` (`user_id`),
  CONSTRAINT `FKct9jfcshu1s6oko3tt7pnw112` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKqtcs65emvosbmm1q7n4bptwil` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrow_transactions`
--

LOCK TABLES `borrow_transactions` WRITE;
/*!40000 ALTER TABLE `borrow_transactions` DISABLE KEYS */;
INSERT INTO `borrow_transactions` VALUES (1,'2024-08-25','2025-09-09 06:02:08.000000',NULL,'2024-09-08',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'BORROWED','2025-09-09 06:02:08.000000',1,3),(2,'2024-08-20','2025-09-09 06:02:08.000000',NULL,'2024-09-03',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'BORROWED','2025-09-09 06:02:08.000000',7,3),(3,'2024-08-28','2025-09-09 06:02:08.000000',NULL,'2024-09-11',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'BORROWED','2025-09-09 06:02:08.000000',2,4),(4,'2024-08-22','2025-09-09 06:02:08.000000',NULL,'2024-09-05',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'BORROWED','2025-09-09 06:02:08.000000',11,4),(5,'2024-08-15','2025-09-09 06:02:08.000000',NULL,'2024-08-29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'OVERDUE','2025-09-09 06:02:08.000000',4,5),(6,'2024-08-18','2025-09-09 06:02:08.000000',NULL,'2024-09-01',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'OVERDUE','2025-09-09 06:02:08.000000',15,5),(7,'2024-07-10','2024-07-10 10:00:00.000000',NULL,'2024-07-24',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'RETURNED','2024-07-20 15:30:00.000000',9,6),(8,'2024-07-15','2024-07-15 14:00:00.000000',NULL,'2024-07-29',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'RETURNED','2024-07-28 11:00:00.000000',10,6),(9,'2024-07-05','2024-07-05 09:00:00.000000',NULL,'2024-07-19',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'RETURNED','2024-07-18 16:45:00.000000',5,7),(10,'2024-06-20','2024-06-20 11:30:00.000000',NULL,'2024-07-04',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'RETURNED','2024-07-02 14:15:00.000000',6,7);
/*!40000 ALTER TABLE `borrow_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservations`
--

DROP TABLE IF EXISTS `reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `expiry_date` date NOT NULL,
  `notes` varchar(500) DEFAULT NULL,
  `notification_date` datetime(6) DEFAULT NULL,
  `notification_sent` bit(1) DEFAULT NULL,
  `queue_position` int DEFAULT NULL,
  `reservation_date` date NOT NULL,
  `status` enum('ACTIVE','AVAILABLE','CANCELLED','EXPIRED','FULFILLED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `book_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrsdd3ib3landfpmgoolccjakt` (`book_id`),
  KEY `FKb5g9io5h54iwl2inkno50ppln` (`user_id`),
  CONSTRAINT `FKb5g9io5h54iwl2inkno50ppln` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKrsdd3ib3landfpmgoolccjakt` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
INSERT INTO `reservations` VALUES (1,'2025-09-09 06:02:08.000000','2024-09-06',NULL,NULL,NULL,1,'2024-08-30','ACTIVE','2025-09-09 06:02:08.000000',4,6),(2,'2025-09-09 06:02:08.000000','2024-09-08',NULL,NULL,NULL,2,'2024-09-01','ACTIVE','2025-09-09 06:02:08.000000',2,7),(3,'2025-09-09 06:02:08.000000','2024-09-09',NULL,NULL,NULL,1,'2024-09-02','ACTIVE','2025-09-09 06:02:08.000000',12,3),(4,'2024-08-15 10:00:00.000000','2024-08-22',NULL,NULL,NULL,1,'2024-08-15','EXPIRED','2024-08-23 00:00:00.000000',1,4),(5,'2024-08-10 14:00:00.000000','2024-08-17',NULL,NULL,NULL,1,'2024-08-10','EXPIRED','2024-08-18 00:00:00.000000',5,5);
/*!40000 ALTER TABLE `reservations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `membership_end_date` datetime(6) DEFAULT NULL,
  `membership_start_date` datetime(6) DEFAULT NULL,
  `membership_type` enum('PREMIUM','STANDARD','STUDENT') DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','MEMBER') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'123 Admin Street, Singapore','2025-09-09 06:02:08.000000','admin.test@library.com','John Smith','2025-12-31 23:59:59.000000','2024-01-01 00:00:00.000000','PREMIUM','admin123','65-9123-4567','ADMIN','2025-09-09 06:02:08.000000','admin'),(2,'456 Library Avenue, Singapore','2025-09-09 06:02:08.000000','girl.admin@library.com','Sarah Johnson','2025-12-31 23:59:59.000000','2024-01-01 00:00:00.000000','PREMIUM','girl123','65-9234-5678','ADMIN','2025-09-09 06:02:08.000000','girl'),(3,'789 Member Road, Singapore','2025-09-09 06:02:08.000000','boy.member@email.com','Alice Wong','2025-12-31 23:59:59.000000','2024-01-15 00:00:00.000000','STANDARD','boy123','65-8123-4567','MEMBER','2025-09-09 06:02:08.000000','boy'),(4,'321 Reader Street, Milky Way','2025-09-09 06:02:08.000000','bob.member@email.com','Bob Tan','2025-12-31 23:59:59.000000','2024-02-01 00:00:00.000000','PREMIUM','bob123','65-8234-5671','MEMBER','2025-09-09 12:54:53.748029','bob'),(5,'654 Book Lane, Singapore','2025-09-09 06:02:08.000000','charlie.member@email.com','Charlie Lim','2025-12-31 23:59:59.000000','2024-08-01 00:00:00.000000','STUDENT','charlie123','65-8345-6789','MEMBER','2025-09-09 06:02:08.000000','charlie'),(6,'987 Study Avenue, Singapore','2025-09-09 06:02:08.000000','diana.member@email.com','Diana Ng','2025-12-31 23:59:59.000000','2024-03-15 00:00:00.000000','STANDARD','diana123','65-8456-7890','MEMBER','2025-09-09 06:02:08.000000','diana'),(7,'159 Knowledge Street, Singapore','2025-09-09 06:02:08.000000','edward.member@email.com','Edward Koh','2025-12-31 23:59:59.000000','2024-04-01 00:00:00.000000','PREMIUM','edward123','65-8567-8901','MEMBER','2025-09-09 06:02:08.000000','edward'),(8,'singapore','2025-09-09 06:32:26.107568','ideal@email.com','idealboy','2026-09-09 06:32:26.101536','2025-09-09 06:32:26.101536','STANDARD','$2a$10$zy.ZQmbD2YP/.RQeH6rXx.sJ4jLScsv2IOoxLG5s9W2zfejOJAxm6','21212121','MEMBER','2025-09-09 06:32:26.107568','ideal');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-09 14:03:48
