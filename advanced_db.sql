-- phpMyAdmin SQL Dump
-- version 4.1.12
-- http://www.phpmyadmin.net
--
-- Φιλοξενητής: 127.0.0.1
-- Χρόνος δημιουργίας: 14 Μαρ 2015 στις 16:55:50
-- Έκδοση διακομιστή: 5.6.16
-- Έκδοση PHP: 5.5.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Βάση δεδομένων: `advanced_db2`
--

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `items`
--

CREATE TABLE IF NOT EXISTS `items` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL DEFAULT 'none',
  `initial_price` double NOT NULL,
  `highest_bid` double NOT NULL DEFAULT '0',
  `holder` varchar(255) NOT NULL DEFAULT 'none',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=8 ;

--
-- Άδειασμα δεδομένων του πίνακα `items`
--

INSERT INTO `items` (`id`, `name`, `description`, `initial_price`, `highest_bid`, `holder`) VALUES
(7, 'ferrari', 'isRed', 1000000, 0, 'georgios');

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `registration`
--

CREATE TABLE IF NOT EXISTS `registration` (
  `bidder_name` varchar(255) NOT NULL,
  `socket_id` int(255) NOT NULL,
  `canBid` int(1) NOT NULL DEFAULT '0',
  `connection_status` int(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`bidder_name`),
  UNIQUE KEY `bidder_name` (`bidder_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
