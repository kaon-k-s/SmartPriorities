-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 13. Aug 2024 um 21:42
-- Server-Version: 10.4.28-MariaDB
-- PHP-Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `taskmanager`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `motivationalquotes`
--

CREATE TABLE `motivationalquotes` (
  `quoteId` int(11) NOT NULL,
  `quoteText` varchar(75) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `motivationalquotes`
--

INSERT INTO `motivationalquotes` (`quoteId`, `quoteText`) VALUES
(1, 'You only fail when you stop trying.');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `tasklist`
--

CREATE TABLE `tasklist` (
  `taskId` int(11) NOT NULL,
  `taskUserIdFK` int(11) DEFAULT NULL,
  `taskName` varchar(50) DEFAULT NULL,
  `taskPriority` int(11) DEFAULT NULL,
  `taskDueDate` date DEFAULT NULL,
  `taskUrgent` tinyint(1) DEFAULT NULL,
  `taskLinkToTaskId` int(11) DEFAULT NULL,
  `taskWorkStudy` tinyint(1) DEFAULT NULL,
  `taskContracts` tinyint(1) DEFAULT NULL,
  `taskPeople` tinyint(1) DEFAULT NULL,
  `taskHealth` tinyint(1) DEFAULT NULL,
  `taskNotes` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `tasklist`
--

INSERT INTO `tasklist` (`taskId`, `taskUserIdFK`, `taskName`, `taskPriority`, `taskDueDate`, `taskUrgent`, `taskLinkToTaskId`, `taskWorkStudy`, `taskContracts`, `taskPeople`, `taskHealth`, `taskNotes`) VALUES
(14, 1, 'Task1', 10, '2024-08-14', 1, -1, 1, 0, 0, 0, 'My first task!'),
(15, 1, 'Task 2', 6, '2024-08-19', 0, -1, 1, 1, 1, 0, ''),
(16, 1, 'Task 3', 4, '2024-07-17', 0, -1, 0, 0, 0, 0, ''),
(17, 1, 'Task 4', 9, '2024-08-17', 0, -1, 0, 0, 0, 0, ''),
(18, 1, 'Task 5', 6, '2024-10-04', 0, -1, 0, 0, 0, 1, ''),
(20, 1, 'Task 6', 10, '2024-11-05', 1, -1, 0, 1, 0, 0, ''),
(21, 1, 'Task 7', 10, '2024-09-07', 1, -1, 1, 1, 1, 1, '');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `user`
--

CREATE TABLE `user` (
  `userId` int(11) NOT NULL,
  `userName` varchar(30) DEFAULT NULL,
  `userEmail` varchar(30) DEFAULT NULL,
  `userPassword` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Daten für Tabelle `user`
--

INSERT INTO `user` (`userId`, `userName`, `userEmail`, `userPassword`) VALUES
(1, 'Karyna', 'karyna@gmail.com', 'kkk');

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `motivationalquotes`
--
ALTER TABLE `motivationalquotes`
  ADD PRIMARY KEY (`quoteId`);

--
-- Indizes für die Tabelle `tasklist`
--
ALTER TABLE `tasklist`
  ADD PRIMARY KEY (`taskId`);

--
-- Indizes für die Tabelle `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`userId`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `motivationalquotes`
--
ALTER TABLE `motivationalquotes`
  MODIFY `quoteId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT für Tabelle `tasklist`
--
ALTER TABLE `tasklist`
  MODIFY `taskId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT für Tabelle `user`
--
ALTER TABLE `user`
  MODIFY `userId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
