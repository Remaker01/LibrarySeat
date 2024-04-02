-- MySQL dump 10.13  Distrib 8.0.20, for Win64 (x86_64)
--
-- Host: localhost    Database: library
-- ------------------------------------------------------
-- Server version	8.0.20

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+08:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `message`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (-10036,'li_si-10036','7e12ae69465c4233273000166d7e1cab150440551392beb823d60b5063358eca','李四','男','15555555550-10036',1,1),(-10034,'wang_feng-10034','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','王峰','男','13555555556-10034',2,1),(-10032,'wang_fen-10032','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','王峰','男','13555555556-10032',2,1),(10000,'admin','97d3231e8a567a8fd170aaf3ce8549ac953ecd8e8ce2bc5fcf1c0de9bcf20f77','王毅','男','13323530987',0,1),(10001,'vvmigcy','5505dbebb65079c169c03559e73f691001106aa4ffd57bd40381f1c27f94ff2a','郑明胜','男','16298856048',2,1),(10002,'li_guangfa','eb5766cba38661499eb78cdb93c09087c4c3d3d1b6ebf258cc11aa500322d632','李广发','男','19677658109',1,1),(10003,'kv8blnn3x','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','孙刚才','男','16152831470',2,1),(10004,'li_hui','eb5766cba38661499eb78cdb93c09087c4c3d3d1b6ebf258cc11aa500322d632','李慧','女','15781330559',1,1),(10005,'li_fan','8430aabb652af5593fe6e3323b13f7510b6763c1c11efa9402f35653816900ac','李凡','女','14382063113',2,1),(10006,'txqyxti','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','钱健志','男','14130994838',2,1),(10007,'e2ws_rw55','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','王霞','女','17449323357',2,1),(10008,'zwgqujyl','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','赵珍瑞','女','17251007563',2,1),(10009,'g2f83d','eb5766cba38661499eb78cdb93c09087c4c3d3d1b6ebf258cc11aa500322d632','孙美','女','15031669226',1,1),(10010,'f4n38xu','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','钱慧','女','18778512991',2,1),(10011,'l8weux','eb5766cba38661499eb78cdb93c09087c4c3d3d1b6ebf258cc11aa500322d632','李毅俊','男','16463314908',1,1),(10012,'lyarru','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','周艳','女','14335777057',2,1),(10013,'geres','eb5766cba38661499eb78cdb93c09087c4c3d3d1b6ebf258cc11aa500322d632','周瑞','女','19611240660',1,1),(10014,'fssd9','eb5766cba38661499eb78cdb93c09087c4c3d3d1b6ebf258cc11aa500322d632','吴良清','男','13476066058',1,1),(10015,'qzw4y3vv','eb5766cba38661499eb78cdb93c09087c4c3d3d1b6ebf258cc11aa500322d632','周保胜','男','13745515637',1,1),(10016,'qvxxswqu','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','孙海海','男','16138862417',2,1),(10017,'dnuqj0','eb5766cba38661499eb78cdb93c09087c4c3d3d1b6ebf258cc11aa500322d632','周利','男','16186538293',1,1),(10018,'xpfvn3wzl','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','周凡','女','17253276192',2,1),(10019,'gxy5zxs','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','王云霞','女','17573136381',2,1),(10020,'amrd5r','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','郑勤美','女','17150031636',2,1),(10021,'wgc9u4d','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','王毅明','男','13191991385',2,1),(10022,'o2g69hys','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','钱雪瑞','女','14414123748',2,1),(10023,'t8muax','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','钱瑞美','女','14591634953',2,1),(10024,'bu233','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','孙新','男','15482665374',2,1),(10025,'mtn0n8p','eb5766cba38661499eb78cdb93c09087c4c3d3d1b6ebf258cc11aa500322d632','李佳','女','15251105356',1,1),(10026,'r6s5xxsq0','eb5766cba38661499eb78cdb93c09087c4c3d3d1b6ebf258cc11aa500322d632','赵全','男','15248822808',1,1),(10027,'olfi370l1','eb5766cba38661499eb78cdb93c09087c4c3d3d1b6ebf258cc11aa500322d632','王利世','男','14628021773',1,1),(10028,'k7mwryrl','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','李霞凡','女','17440487654',2,1),(10029,'klwgaa8','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','李清','男','16368908470',2,1),(10030,'c5kzl7','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','钱雪','女','18235230751',2,1),(10031,'sun_zhen','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','孙振','男','17727737747',2,1),(10034,'li_mou','323675e44352fbf1c95e2960a39e9e4d7b0b84aec956f5bad5d2b1b1ec236afd','李某','男','13333334444',2,1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (10000,'2024-03-08 21:34:02','新阅览室已上线！','管理员新增的阅览室 四楼临时阅览室 现已可用！'),(10000,'2024-03-09 10:01:40','座位添加通知','管理员在阅览室四楼临时阅览室添加了1号座位'),(10000,'2024-03-09 10:01:42','座位添加通知','管理员在阅览室四楼临时阅览室添加了2号座位'),(10000,'2024-03-18 09:04:51','座位添加通知','管理员在阅览室综合阅览区3添加了5号座位');
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (3,10000,10005,'2024-02-23 10:38:07','2024-02-23 10:41:09','2024-02-23 11:44:20'),(3,10000,10005,'2024-03-07 09:13:11','2024-03-07 09:28:01','2024-03-07 19:26:31'),(4,10000,10031,'2024-02-21 20:28:37','2024-02-21 20:35:09','2024-02-21 20:39:36'),(5,10000,10031,'2024-03-19 10:12:16','2024-03-19 10:14:26','2024-03-19 20:09:36'),(1,10001,10006,'2024-03-18 18:48:35','2024-03-18 18:50:25','2024-03-18 21:33:00'),(4,10001,10031,'2024-03-11 10:54:15','2024-03-11 10:58:33','2024-03-11 19:01:34'),(1,10002,10001,'2024-03-12 12:55:25','2024-03-12 12:56:09','2024-03-12 17:02:29'),(1,10002,10005,'2024-03-11 13:47:38','2024-03-11 13:49:02','2024-03-11 15:06:17'),(1,10004,10005,'2024-03-03 17:41:00','2024-03-03 17:45:19','2024-03-03 20:13:10'),(1,10004,10005,'2024-03-20 10:46:44',NULL,'2024-03-20 11:17:09');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (10000,'综合阅览区1',10002),(10001,'综合阅览区2',10002),(10002,'综合阅览区3',10004),(10003,'电子阅览区',10009),(10004,'报刊杂志阅览区',10004),(10005,'考研考编阅览区',10009),(10006,'四楼临时阅览室',10025);
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `seat`
--

LOCK TABLES `seat` WRITE;
/*!40000 ALTER TABLE `seat` DISABLE KEYS */;
INSERT INTO `seat` VALUES (1,10000,0),(1,10001,0),(1,10002,0),(1,10003,0),(1,10004,0),(1,10005,0),(1,10006,0),(2,10000,0),(2,10001,0),(2,10002,0),(2,10003,0),(2,10004,0),(2,10005,0),(2,10006,0),(3,10000,0),(3,10001,0),(3,10002,0),(3,10003,0),(3,10004,0),(3,10005,0),(4,10000,0),(4,10001,0),(4,10002,0),(5,10000,0),(5,10001,0),(5,10002,0);
/*!40000 ALTER TABLE `seat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `users`
--

--
-- Dumping events for database 'library'
--
/*!50106 SET @save_time_zone= @@TIME_ZONE */ ;
/*!50106 DROP EVENT IF EXISTS `auto_signout` */;
DELIMITER ;;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;;
/*!50003 SET character_set_client  = utf8mb4 */ ;;
/*!50003 SET character_set_results = utf8mb4 */ ;;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;;
/*!50003 SET @saved_time_zone      = @@time_zone */ ;;
/*!50003 SET time_zone             = '+08:00' */ ;;
/*!50106 CREATE*/ /*!50117 DEFINER=`root`@`localhost`*/ /*!50106 EVENT `auto_signout` ON SCHEDULE EVERY 30 SECOND STARTS '2024-02-19 12:32:39' ON COMPLETION PRESERVE ENABLE DO update reservation,seat set signout_time=NOW(),seat.status=0

    where reservation.seatid=seat.seatid

      and reservation.roomid=seat.roomid

      and signin_time is null

      and signout_time is null

      and TIMESTAMPDIFF(minute,reservation_time,NOW())>=30 */ ;;
/*!50003 SET time_zone             = @saved_time_zone */ ;;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;;
/*!50003 SET character_set_client  = @saved_cs_client */ ;;
/*!50003 SET character_set_results = @saved_cs_results */ ;;
/*!50003 SET collation_connection  = @saved_col_connection */ ;;
/*!50106 DROP EVENT IF EXISTS `auto_signout_every_night` */;;
DELIMITER ;;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;;
/*!50003 SET character_set_client  = utf8mb4 */ ;;
/*!50003 SET character_set_results = utf8mb4 */ ;;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;;
/*!50003 SET @saved_time_zone      = @@time_zone */ ;;
/*!50003 SET time_zone             = '+08:00' */ ;;
/*!50106 CREATE*/ /*!50117 DEFINER=`root`@`localhost`*/ /*!50106 EVENT `auto_signout_every_night` ON SCHEDULE EVERY 1 DAY STARTS '2024-03-02 22:00:00' ON COMPLETION PRESERVE ENABLE DO update reservation,seat set signout_time=NOW(),seat.status=0
        where reservation.seatid=seat.seatid
        and reservation.roomid=seat.roomid
        and signout_time is null */ ;;
/*!50003 SET time_zone             = @saved_time_zone */ ;;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;;
/*!50003 SET character_set_client  = @saved_cs_client */ ;;
/*!50003 SET character_set_results = @saved_cs_results */ ;;
/*!50003 SET collation_connection  = @saved_col_connection */ ;;
DELIMITER ;
/*!50106 SET TIME_ZONE= @save_time_zone */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-03-23 11:42:38
