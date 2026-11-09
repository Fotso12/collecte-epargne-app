-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : jeu. 15 jan. 2026 à 12:28
-- Version du serveur : 8.2.0
-- Version de PHP : 8.2.13

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `collecte-epargne`
--

-- --------------------------------------------------------

--
-- Structure de la table `agence_zone`
--

DROP TABLE IF EXISTS `agence_zone`;
CREATE TABLE IF NOT EXISTS `agence_zone` (
  `id_agence` int NOT NULL AUTO_INCREMENT,
  `adresse` varchar(255) DEFAULT NULL,
  `code` varchar(50) NOT NULL,
  `date_creation` datetime(6) NOT NULL,
  `description` tinytext,
  `nom` varchar(100) NOT NULL,
  `quartier` varchar(50) NOT NULL,
  `statut` tinyint NOT NULL,
  `telephone` varchar(20) NOT NULL,
  `ville` varchar(50) NOT NULL,
  `position` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_agence`)
) ;

--
-- Déchargement des données de la table `agence_zone`
--

INSERT INTO `agence_zone` (`id_agence`, `adresse`, `code`, `date_creation`, `description`, `nom`, `quartier`, `statut`, `telephone`, `ville`, `position`) VALUES
(1, 'BONAMOUSSADI', 'AGC-EDE-202597165', '2025-12-27 15:06:45.488192', 'COLLECTE ET EPAGNE DE LOGBESSOU', 'BATAGENCE', 'LOGBESSOU', 0, '6957515684', 'DOUALA', '3QMJ+64 Douala'),
(3, 'IUC', 'AG005', '2026-01-09 12:29:20.264000', '', 'N\'FO', 'LOGBESSOU', 0, '56465464', 'DOUALA', NULL),
(4, '192 RUE VOG 1ER', 'AG002', '2026-01-10 15:05:08.009000', '', 'BATAGENCes', 'VOGADA', 1, '6957515644', 'YAOUNDE', 'VG7H+QHG'),
(5, 'soung1', 'AGC-EDE-202697184', '2026-01-10 16:48:09.391000', '', 'Brutus', 'soung', 1, '65546465', 'Edea', 'R43H+89 Edéa'),
(6, 'BONAMOUSSADI', 'dfdsffds', '2026-01-10 19:12:46.690000', '', 'Yom', 'LOGBESSOU', 1, '4145412', 'DOUALA', '');

-- --------------------------------------------------------

--
-- Structure de la table `client`
--

DROP TABLE IF EXISTS `client`;
CREATE TABLE IF NOT EXISTS `client` (
  `numero_client` bigint NOT NULL AUTO_INCREMENT,
  `adresse` varchar(255) NOT NULL,
  `cni_recto_path` varchar(255) NOT NULL,
  `cni_verso_path` varchar(255) NOT NULL,
  `code_client` varchar(50) NOT NULL,
  `date_naissance` date NOT NULL,
  `lieu_naissance` varchar(100) NOT NULL,
  `num_cni` varchar(50) NOT NULL,
  `photo_path` varchar(255) NOT NULL,
  `profession` varchar(100) NOT NULL,
  `score_epargne` int DEFAULT NULL,
  `type_cni` tinyint NOT NULL,
  `collecteur_assigne` int NOT NULL,
  `login` varchar(50) NOT NULL,
  `ville` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`numero_client`),
  UNIQUE KEY `UKlaona9qitc0p21nb935x5tvx2` (`code_client`),
  UNIQUE KEY `UKgg7oafsubulcaholss4i5kfgl` (`login`),
  KEY `FK2pps5fl79e1lmmw33jsll2y38` (`collecteur_assigne`)
) ;

--
-- Déchargement des données de la table `client`
--

INSERT INTO `client` (`numero_client`, `adresse`, `cni_recto_path`, `cni_verso_path`, `code_client`, `date_naissance`, `lieu_naissance`, `num_cni`, `photo_path`, `profession`, `score_epargne`, `type_cni`, `collecteur_assigne`, `login`, `ville`) VALUES
(1, '123 Rue de la Paix, Dakar', '/uploads/cni/recto_client1.jpg', '/uploads/cni/verso_client1.jpg', 'CLI001', '1990-05-15', 'Dakar', '123456789', '/uploads/photos/client1.jpg', 'Enseignant', 85, 1, 34, 'client1', NULL),
(4, '123 Rue de la Paix, Dakars', '/uploads/cni/recto_client1.jpg', '/uploads/cni/verso_client1.jpg', 'CLT202599167', '1990-05-15', 'Dakar', '123456789d', '/uploads/photos/client1.jpg', 'Enseignant', 85, 1, 2, 'soh', NULL),
(145, 'Douala', 'r110.png', 'v110.png', 'CLT202610128', '1990-02-15', 'Douala', 'CNI_A1', 'p110.png', 'Artisan', 100, 1, 7, 'client_7_01', 'Douala'),
(146, 'Douala', 'r111.png', 'v111.png', 'CLT202617602', '1985-06-20', 'Douala', 'CNI_A2', 'p111.png', 'Commercant', 45, 1, 7, 'client_7_02', 'Douala'),
(147, 'Edea', 'r112.png', 'v112.png', 'CLT202660768', '1993-11-10', 'Edea', 'CNI_A3', 'p112.png', 'Chauffeur', 60, 0, 7, 'client_7_03', 'Douala'),
(148, 'Douala', 'r113.png', 'v113.png', 'CLT202686474', '1988-04-25', 'Douala', 'CNI_A4', 'p113.png', 'Couturiere', 150, 1, 7, 'client_7_04', 'Douala'),
(149, 'Douala', 'r114.png', 'v114.png', 'CLT202650350', '1995-09-12', 'Douala', 'CNI_A5', 'p114.png', 'Mecanicien', 30, 1, 7, 'client_7_05', 'Douala'),
(150, 'Limbe', 'r115.png', 'v115.png', 'CLT202699566', '1982-01-30', 'Limbe', 'CNI_A6', 'p115.png', 'Pecheur', 200, 0, 7, 'client_7_06', 'Douala'),
(151, 'Douala', 'r116.png', 'v116.png', 'CLT202619301', '1991-07-05', 'Douala', 'CNI_A7', 'p116.png', 'Infirmiere', 95, 1, 7, 'client_7_07', 'Douala'),
(152, 'Kribi', 'r117.png', 'v117.png', 'CLT202642234', '1987-12-14', 'Kribi', 'CNI_A8', 'p117.png', 'Hotelier', 70, 1, 7, 'client_7_08', 'Douala'),
(153, 'Douala', 'r118.png', 'v118.png', 'CLT202621245', '1994-03-22', 'Douala', 'CNI_A9', 'p118.png', 'Vendeur', 55, 0, 7, 'client_7_09', 'Douala'),
(154, 'Douala', 'r119.png', 'v119.png', 'CLT202668697', '1989-10-18', 'Douala', 'CNI_A10', 'p119.png', 'Comptable', 300, 1, 7, 'client_7_10', 'Douala'),
(155, 'Yaounde', 'r120.png', 'v120.png', 'CLT202661864', '1990-05-22', 'Yaounde', 'CNI_B1', 'p120.png', 'Informaticien', 120, 1, 9, 'client_9_01', 'Douala'),
(156, 'Yaounde', 'r121.png', 'v121.png', 'CLT202612257', '1986-08-05', 'Yaounde', 'CNI_B2', 'p121.png', 'Avocat', 500, 0, 9, 'client_9_02', 'Douala'),
(157, 'Bafoussam', 'r122.png', 'v122.png', 'CLT202658669', '1992-02-12', 'Bafoussam', 'CNI_B3', 'p122.png', 'Agriculteur', 40, 1, 9, 'client_9_03', 'Douala'),
(158, 'Yaounde', 'r123.png', 'v123.png', 'CLT202653886', '1984-11-25', 'Yaounde', 'CNI_B4', 'p123.png', 'Menagere', 35, 1, 9, 'client_9_04', 'Douala'),
(159, 'Yaounde', 'r124.png', 'v124.png', 'CLT202633355', '1996-04-14', 'Yaounde', 'CNI_B5', 'p124.png', 'Etudiant', 20, 0, 9, 'client_9_05', 'Douala'),
(160, 'Mbalmayo', 'r125.png', 'v125.png', 'CLT202612233', '1981-12-02', 'Mbalmayo', 'CNI_B6', 'p125.png', 'Menuisier', 110, 1, 9, 'client_9_06', 'Douala'),
(161, 'Yaounde', 'r126.png', 'v126.png', 'CLT202693649', '1993-01-28', 'Yaounde', 'CNI_B7', 'p126.png', 'Coiffeuse', 85, 1, 9, 'client_9_07', 'Douala'),
(163, 'Yaounde', 'r128.png', 'v128.png', 'CLT202680200', '1990-07-30', 'Yaounde', 'CNI_B9', 'p128.png', 'Securite', 90, 1, 34, 'client_9_09', 'Douala'),
(164, 'Yaounde', 'r129.png', 'v129.png', 'CLT202653734', '1989-06-05', 'Yaounde', 'CNI_B10', 'p129.png', 'Secretaire', 145, 1, 34, 'client_9_10', 'Douala'),
(165, 'iuc-logbessou', 'uploads\\clients\\cni_recto\\89810b65-b6d6-4203-a08f-25a381d17a17_scaled_1000664730.jpg', 'uploads\\clients\\cni_verso\\4966b264-0faa-4823-b1ef-4f933b21b382_scaled_1000663264.jpg', 'CLI1768442972409', '2000-01-01', 'Douala', '62735265', 'uploads\\clients\\photos\\f8fcf796-f4cd-47bb-9e7c-7da29049215f_scaled_1000661799.jpg', 'Enseignant', NULL, 1, 34, 'CLI_fizz_971977', 'douala');

-- --------------------------------------------------------

--
-- Structure de la table `clients`
--

DROP TABLE IF EXISTS `clients`;
CREATE TABLE IF NOT EXISTS `clients` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` tinytext,
  `avatar_url` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `date_naissance` date DEFAULT NULL,
  `full_name` varchar(150) NOT NULL,
  `identity_number` varchar(80) DEFAULT NULL,
  `identity_type` varchar(40) DEFAULT NULL,
  `lieu_naissance` varchar(100) DEFAULT NULL,
  `phone` varchar(40) DEFAULT NULL,
  `profession` varchar(100) DEFAULT NULL,
  `score_epargne` int DEFAULT NULL,
  `status` enum('ACTIVE','SUSPENDED','CLOSED') DEFAULT NULL,
  `collector_id` varchar(50) DEFAULT NULL,
  `institution_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3r8qetq5twytljpg9nf4y44t9` (`collector_id`),
  KEY `FKktfm0m7y3yje2f1okl6opmwv5` (`institution_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `clients`
--

INSERT INTO `clients` (`id`, `address`, `avatar_url`, `created_at`, `date_naissance`, `full_name`, `identity_number`, `identity_type`, `lieu_naissance`, `phone`, `profession`, `score_epargne`, `status`, `collector_id`, `institution_id`) VALUES
(1, 'iuc-logbessou', NULL, '2026-01-15 02:09:32.669627', NULL, 'Jo Fizz', '62735265', 'CNI', NULL, '698457878', NULL, NULL, 'ACTIVE', 'collecteur1', 1);

-- --------------------------------------------------------

--
-- Structure de la table `compte`
--

DROP TABLE IF EXISTS `compte`;
CREATE TABLE IF NOT EXISTS `compte` (
  `id_compte` varchar(50) NOT NULL,
  `date_cloture` date DEFAULT NULL,
  `date_derniere_transaction` datetime(6) DEFAULT NULL,
  `date_ouverture` date NOT NULL,
  `motif_blocage` tinytext,
  `num_compte` varchar(50) NOT NULL,
  `solde` decimal(15,2) DEFAULT NULL,
  `solde_disponible` decimal(15,2) DEFAULT NULL,
  `statut` tinyint DEFAULT NULL,
  `taux_bonus` decimal(5,2) DEFAULT NULL,
  `taux_penalite` decimal(5,2) DEFAULT NULL,
  `code_client` bigint NOT NULL,
  `id_type` int NOT NULL,
  PRIMARY KEY (`id_compte`),
  KEY `FK4j39u1ymy78ien5aes05ut81e` (`code_client`),
  KEY `FKf4tk0h14gnm9ffdsu4ep1548m` (`id_type`)
) ;

--
-- Déchargement des données de la table `compte`
--

INSERT INTO `compte` (`id_compte`, `date_cloture`, `date_derniere_transaction`, `date_ouverture`, `motif_blocage`, `num_compte`, `solde`, `solde_disponible`, `statut`, `taux_bonus`, `taux_penalite`, `code_client`, `id_type`) VALUES
('COMPTE_CBDE748D', NULL, NULL, '2026-01-15', NULL, 'NUM_CD1BAB5A', 1000.00, 2000.00, 0, NULL, NULL, 165, 1),
('CPT001', NULL, NULL, '2025-12-27', NULL, 'NUMCPT001', 400.00, 500.00, 0, NULL, NULL, 1, 1);

-- --------------------------------------------------------

--
-- Structure de la table `compte_cotisation`
--

DROP TABLE IF EXISTS `compte_cotisation`;
CREATE TABLE IF NOT EXISTS `compte_cotisation` (
  `id` varchar(50) NOT NULL,
  `date_adhesion` date NOT NULL,
  `montant_total_verse` decimal(15,2) DEFAULT NULL,
  `nombre_retards` int DEFAULT NULL,
  `nombre_versements` int DEFAULT NULL,
  `prochaine_echeance` date DEFAULT NULL,
  `statut` tinyint DEFAULT NULL,
  `id_compte` varchar(50) NOT NULL,
  `id_plan` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcp4gtgdb4ugty50jnq7317qta` (`id_compte`),
  KEY `FKcpd1wsef5d4y7hpgxgnax4q0b` (`id_plan`)
) ;

--
-- Déchargement des données de la table `compte_cotisation`
--

INSERT INTO `compte_cotisation` (`id`, `date_adhesion`, `montant_total_verse`, `nombre_retards`, `nombre_versements`, `prochaine_echeance`, `statut`, `id_compte`, `id_plan`) VALUES
('COT001', '2025-12-27', 200.00, 0, 2, '2026-01-27', 0, 'CPT001', 'PLAN001');

-- --------------------------------------------------------

--
-- Structure de la table `demande_ouverture_compte`
--

DROP TABLE IF EXISTS `demande_ouverture_compte`;
CREATE TABLE IF NOT EXISTS `demande_ouverture_compte` (
  `id_demande` bigint NOT NULL AUTO_INCREMENT,
  `date_demande` datetime(6) NOT NULL,
  `date_validation` datetime(6) DEFAULT NULL,
  `montant_initial` decimal(15,2) DEFAULT NULL,
  `motif` tinytext,
  `motif_rejet` tinytext,
  `statut` enum('EN_ATTENTE','VALIDEE','REJETEE') NOT NULL,
  `code_client` bigint NOT NULL,
  `id_superviseur_validateur` int DEFAULT NULL,
  `id_type_compte` int NOT NULL,
  PRIMARY KEY (`id_demande`),
  KEY `FKsh6w6motnflh68gas4776c73d` (`code_client`),
  KEY `FKivlxs4amr2qdicx1jty0k4q0n` (`id_superviseur_validateur`),
  KEY `FKeeydx45m0vw8la073l76aied2` (`id_type_compte`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `demande_ouverture_compte`
--

INSERT INTO `demande_ouverture_compte` (`id_demande`, `date_demande`, `date_validation`, `montant_initial`, `motif`, `motif_rejet`, `statut`, `code_client`, `id_superviseur_validateur`, `id_type_compte`) VALUES
(1, '2026-01-15 02:17:04.238923', '2026-01-15 03:51:43.501669', 2000.00, 'epargner', NULL, 'VALIDEE', 165, 30, 1);

-- --------------------------------------------------------

--
-- Structure de la table `device_token`
--

DROP TABLE IF EXISTS `device_token`;
CREATE TABLE IF NOT EXISTS `device_token` (
  `id_device_token` bigint NOT NULL AUTO_INCREMENT,
  `actif` bit(1) NOT NULL,
  `device_type` varchar(50) DEFAULT NULL,
  `token` varchar(255) NOT NULL,
  `login_utilisateur` varchar(50) NOT NULL,
  PRIMARY KEY (`id_device_token`),
  UNIQUE KEY `UKoaccue9kxei35rbe5thnv18ye` (`token`),
  KEY `FKcwpv15umrnt1rch4yvm2d0y1b` (`login_utilisateur`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `employe`
--

DROP TABLE IF EXISTS `employe`;
CREATE TABLE IF NOT EXISTS `employe` (
  `id_employe` int NOT NULL AUTO_INCREMENT,
  `commission_taux` decimal(5,2) DEFAULT NULL,
  `date_embauche` date NOT NULL,
  `matricule` varchar(50) NOT NULL,
  `type_employe` enum('CAISSIER','COLLECTEUR','SUPERVISEUR') NOT NULL,
  `id_agence` int NOT NULL,
  `superviseur_id` int DEFAULT NULL,
  `login` varchar(50) NOT NULL,
  PRIMARY KEY (`id_employe`),
  UNIQUE KEY `UKmdhj664mx045boi1nic5i8o68` (`login`),
  KEY `FKfbw7jtnfuh8nqyher3hritfml` (`id_agence`),
  KEY `FKayofc6vrp428ew0udu84sftma` (`superviseur_id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `employe`
--

INSERT INTO `employe` (`id_employe`, `commission_taux`, `date_embauche`, `matricule`, `type_employe`, `id_agence`, `superviseur_id`, `login`) VALUES
(1, 5.00, '2020-12-27', 'SUP001', 'SUPERVISEUR', 5, 1, 'superviseur1'),
(2, 5.00, '2023-12-27', 'COL001', 'COLLECTEUR', 1, NULL, 'collecteur1'),
(3, 10.00, '2023-01-01', 'CAIS202695087', 'CAISSIER', 1, NULL, 'cas'),
(5, NULL, '2026-01-03', 'CAIS202678362', 'CAISSIER', 1, NULL, 'Fotso'),
(6, 0.00, '2026-01-03', 'CAIS202634009', 'CAISSIER', 1, NULL, 'free'),
(7, 3.00, '2026-01-03', 'COLL202675898', 'COLLECTEUR', 1, NULL, 'toto1'),
(9, 1.00, '2026-01-03', 'COLL202661452', 'COLLECTEUR', 1, NULL, 'jo1'),
(30, 0.00, '2026-01-08', 'SUP202643296', 'SUPERVISEUR', 1, 1, 'testuser'),
(32, 0.00, '2026-01-10', 'SUP202654166', 'SUPERVISEUR', 1, NULL, 'brawn1'),
(34, 2.00, '2026-01-14', 'COLL202634885', 'COLLECTEUR', 1, 1, 'frees'),
(37, 0.00, '2026-01-15', 'CAIS202668081', 'CAISSIER', 1, NULL, 'fro1');

-- --------------------------------------------------------

--
-- Structure de la table `institutions`
--

DROP TABLE IF EXISTS `institutions`;
CREATE TABLE IF NOT EXISTS `institutions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(30) NOT NULL,
  `contact_email` varchar(120) DEFAULT NULL,
  `contact_phone` varchar(40) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `name` varchar(150) NOT NULL,
  `timezone` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_90wcwrx6ap068pspum25xhtyu` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `institutions`
--

INSERT INTO `institutions` (`id`, `code`, `contact_email`, `contact_phone`, `created_at`, `name`, `timezone`) VALUES
(1, 'DEF001', 'contact@institution.com', '+2250100000001', '2026-01-12 19:53:38.893608', 'Institution par Défaut', 'Africa/Abidjan');

-- --------------------------------------------------------

--
-- Structure de la table `notification`
--

DROP TABLE IF EXISTS `notification`;
CREATE TABLE IF NOT EXISTS `notification` (
  `id_notification` varchar(50) NOT NULL,
  `categorie` tinyint NOT NULL,
  `code_client` varchar(50) DEFAULT NULL,
  `date_creation` datetime(6) DEFAULT NULL,
  `date_envoi` datetime(6) DEFAULT NULL,
  `date_lecture` datetime(6) DEFAULT NULL,
  `erreur_envoi` tinytext,
  `message` tinytext NOT NULL,
  `statut` varchar(20) NOT NULL,
  `titre` varchar(100) DEFAULT NULL,
  `type` tinyint NOT NULL,
  `id_transaction` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_notification`),
  KEY `FKp94qdnc6smrkgd99t0pe954y0` (`id_transaction`)
) ;

--
-- Déchargement des données de la table `notification`
--

INSERT INTO `notification` (`id_notification`, `categorie`, `code_client`, `date_creation`, `date_envoi`, `date_lecture`, `erreur_envoi`, `message`, `statut`, `titre`, `type`, `id_transaction`) VALUES
('0243b938-8e49-4234-b621-81f500f0b6a4', 2, NULL, '2025-12-31 10:40:33.517977', NULL, NULL, NULL, 'Ceci est un test d\'envoi d\'email', 'CREE', 'Test Email', 1, NULL),
('1536016a-cb01-45c1-a3c4-ad999bc1f78a', 2, 'CLT202580551', '2025-12-31 10:57:04.562823', '2025-12-31 11:04:10.815663', NULL, 'Could not initialize proxy [com.collecte_epargne.collecte_epargne.entities.Utilisateur#fo1] - no session', 'Ceci est un test d\'envoi d\'email', 'ERREUR', 'Test Email', 1, NULL),
('159e7659-19e6-45cb-9a5f-b6e98b0fee01', 2, 'CLT202580551', '2025-12-31 11:43:39.396205', '2025-12-31 11:43:41.851259', NULL, 'Aucun device token trouvé', 'Un test d\'envoi dE NOTIFICATION PUSH en utilisant rabbitmq', 'ERREUR', 'Test Email', 3, NULL),
('71c76e6a-49f9-4d25-a196-ef771109b9d1', 2, 'CLT202580551', '2025-12-31 11:20:10.926512', '2025-12-31 11:20:11.772817', '2025-12-31 11:42:41.972368', 'Could not initialize proxy [com.collecte_epargne.collecte_epargne.entities.Utilisateur#fo1] - no session', 'Ceci est un test d\'envoi d\'email en utilisant rabbitmq', 'LU', 'Test Email', 1, NULL),
('ac671da3-624f-4ab6-b1ed-e62983e3260a', 2, 'CLT202580551', '2025-12-31 11:30:06.514146', '2025-12-31 11:30:07.529712', NULL, NULL, 'Un test d\'envoi d\'email en utilisant rabbitmq', 'ENVOYE', 'Test Email', 2, NULL),
('c5bf4ad5-31b7-46d1-a5ad-cca289cfb801', 2, NULL, '2025-12-31 10:48:24.180761', '2025-12-31 11:04:10.214884', NULL, NULL, 'Ceci est un test d\'envoi d\'email', 'ENVOYE', 'Test Email', 1, NULL),
('e6d8c7c5-45c2-484a-8416-95d214ac7420', 2, 'CLT202580551', '2025-12-31 11:28:54.219713', '2025-12-31 11:29:02.232083', NULL, NULL, 'Un test d\'envoi d\'email en utilisant rabbitmq', 'ENVOYE', 'Test Email', 1, NULL),
('NOT001', 0, 'CLI001', '2025-12-27 15:06:46.283098', '2025-12-27 15:06:46.283098', NULL, NULL, 'Votre dépôt de 200.00 FCFA a été effectué avec succès.', 'ENVOYE', 'Dépôt effectué', 1, 'TRX001');

-- --------------------------------------------------------

--
-- Structure de la table `plan_cotisation`
--

DROP TABLE IF EXISTS `plan_cotisation`;
CREATE TABLE IF NOT EXISTS `plan_cotisation` (
  `id_plan` varchar(50) NOT NULL,
  `date_debut` date NOT NULL,
  `date_fin` date DEFAULT NULL,
  `duree_jours` int DEFAULT NULL,
  `frequence` tinyint NOT NULL,
  `montant_attendu` decimal(15,2) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `statut` tinyint NOT NULL,
  `taux_penalite_retard` decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (`id_plan`)
) ;

--
-- Déchargement des données de la table `plan_cotisation`
--

INSERT INTO `plan_cotisation` (`id_plan`, `date_debut`, `date_fin`, `duree_jours`, `frequence`, `montant_attendu`, `nom`, `statut`, `taux_penalite_retard`) VALUES
('48a74c94-354c-492b-85ea-e30ef5ada525', '2026-01-10', '2026-01-10', 1, 0, 1600.00, 'JOURNALIERE', 0, 0.50),
('PLAN001', '2025-12-27', '2026-12-27', 365, 2, 1250.00, 'Plan Épargne Mensuel', 0, 2.00);

-- --------------------------------------------------------

--
-- Structure de la table `rapport_collecteur`
--

DROP TABLE IF EXISTS `rapport_collecteur`;
CREATE TABLE IF NOT EXISTS `rapport_collecteur` (
  `id_rapport` varchar(50) NOT NULL,
  `commentaire_superviseur` tinytext,
  `date_generation` datetime(6) DEFAULT NULL,
  `date_rapport` date NOT NULL,
  `date_validation` datetime(6) DEFAULT NULL,
  `id_employe` varchar(50) NOT NULL,
  `nombre_clients_visites` int DEFAULT NULL,
  `nombre_transactions` int DEFAULT NULL,
  `solde_collecteur` decimal(15,2) DEFAULT NULL,
  `statut_rapport` tinyint DEFAULT NULL,
  `total_depot` decimal(15,2) DEFAULT NULL,
  `total_retrait` decimal(15,2) DEFAULT NULL,
  PRIMARY KEY (`id_rapport`)
) ;

--
-- Déchargement des données de la table `rapport_collecteur`
--

INSERT INTO `rapport_collecteur` (`id_rapport`, `commentaire_superviseur`, `date_generation`, `date_rapport`, `date_validation`, `id_employe`, `nombre_clients_visites`, `nombre_transactions`, `solde_collecteur`, `statut_rapport`, `total_depot`, `total_retrait`) VALUES
('RAP001', 'Rapport validé.', '2025-12-27 15:06:46.307610', '2025-12-27', NULL, 'COL001', 1, 2, 250.00, 3, 250.00, 0.00),
('RAP002', 'Rapport validé.', '2026-01-05 15:06:46.307610', '2026-01-05', NULL, 'COL001', 6, 5, 600.00, 3, 250.00, 0.00);

-- --------------------------------------------------------

--
-- Structure de la table `recu`
--

DROP TABLE IF EXISTS `recu`;
CREATE TABLE IF NOT EXISTS `recu` (
  `id_recu` varchar(50) NOT NULL,
  `contenu` tinytext,
  `date_generation` datetime(6) DEFAULT NULL,
  `fichier_path` varchar(255) DEFAULT NULL,
  `format` tinyint NOT NULL,
  `id_transaction` varchar(50) NOT NULL,
  PRIMARY KEY (`id_recu`),
  UNIQUE KEY `UKky4drdtav2i9y8ek5yq80ehot` (`id_transaction`)
) ;

--
-- Déchargement des données de la table `recu`
--

INSERT INTO `recu` (`id_recu`, `contenu`, `date_generation`, `fichier_path`, `format`, `id_transaction`) VALUES
('REC001', 'Contenu du reçu en format texte.', '2025-12-27 15:06:46.342812', '/uploads/recus/recu001.pdf', 0, 'TRX001');

-- --------------------------------------------------------

--
-- Structure de la table `role`
--

DROP TABLE IF EXISTS `role`;
CREATE TABLE IF NOT EXISTS `role` (
  `id_role` int NOT NULL AUTO_INCREMENT,
  `code` varchar(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `nom` varchar(50) NOT NULL,
  PRIMARY KEY (`id_role`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `role`
--

INSERT INTO `role` (`id_role`, `code`, `description`, `nom`) VALUES
(1, 'SUP', 'Rôle superviseur', 'SUPERVISEUR'),
(2, 'CLI', 'Rôle client', 'CLIENT'),
(3, 'COLL', 'Rôle collecteur', 'COLLECTEUR'),
(4, 'CAIS', 'Rôle caissier', 'CAISSIER'),
(6, 'admin', 'Accès complet au système', 'Administrateur'),
(7, 'supervisor', 'Supervise les agents collecteurs', 'Superviseur'),
(8, 'collector', 'Agent de terrain pour la collecte d\'épargne', 'Agent collecteur'),
(9, 'caissier', 'Caissier pour les opérations bancaires', 'Caissier'),
(10, 'auditor', 'Contrôle et audit des opérations', 'Auditeur'),
(11, 'client', 'Compte client épargnant (ajouté pour inscription app mobile)', 'Client');

-- --------------------------------------------------------

--
-- Structure de la table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
CREATE TABLE IF NOT EXISTS `transaction` (
  `id_transaction` varchar(50) NOT NULL,
  `date_transaction` datetime(6) DEFAULT NULL,
  `date_validation_caisse` datetime(6) DEFAULT NULL,
  `date_validation_superviseur` datetime(6) DEFAULT NULL,
  `description` tinytext,
  `hash_transaction` varchar(255) DEFAULT NULL,
  `mode_transaction` tinyint DEFAULT NULL,
  `montant` decimal(15,2) NOT NULL,
  `motif_rejet` tinytext,
  `reference` varchar(50) NOT NULL,
  `signature_client` tinytext,
  `solde_apres` decimal(15,2) NOT NULL,
  `solde_avant` decimal(15,2) NOT NULL,
  `statut` tinyint DEFAULT NULL,
  `type_transaction` tinyint NOT NULL,
  `id_caissier_validateur` int DEFAULT NULL,
  `id_compte` varchar(50) NOT NULL,
  `id_employe_initiateur` int DEFAULT NULL,
  `id_superviseur_validateur` int DEFAULT NULL,
  PRIMARY KEY (`id_transaction`),
  KEY `FKc5holi1ndwvm8m1cjm6ns8f0x` (`id_caissier_validateur`),
  KEY `FK41hs9ufmm78trxw62c7s0q9xw` (`id_compte`),
  KEY `FK6l6bn7a4q4wjqp9t582c3cgdg` (`id_employe_initiateur`),
  KEY `FKh00iqnsfe2nc413jrp6m9j4rx` (`id_superviseur_validateur`)
) ;

--
-- Déchargement des données de la table `transaction`
--

INSERT INTO `transaction` (`id_transaction`, `date_transaction`, `date_validation_caisse`, `date_validation_superviseur`, `description`, `hash_transaction`, `mode_transaction`, `montant`, `motif_rejet`, `reference`, `signature_client`, `solde_apres`, `solde_avant`, `statut`, `type_transaction`, `id_caissier_validateur`, `id_compte`, `id_employe_initiateur`, `id_superviseur_validateur`) VALUES
('TRX001', '2025-12-27 15:06:46.172326', NULL, '2026-01-10 20:53:59.848883', NULL, NULL, NULL, 200.00, 'Solde trop bas', 'REF001', NULL, 700.00, 500.00, 3, 0, 2, 'CPT001', 2, 30),
('TXN-20260115-404948', '2026-01-15 06:30:56.028161', '2026-01-15 07:36:54.977950', '2026-01-15 07:41:54.623780', NULL, NULL, NULL, 100.00, NULL, 'TXN-20260115-927780', NULL, 400.00, 500.00, 3, 1, 37, 'CPT001', 34, 30),
('TXN-20260115-617945', '2026-01-15 08:44:41.288041', '2026-01-15 07:48:14.587338', '2026-01-15 07:48:37.878202', NULL, NULL, NULL, 1000.00, NULL, 'TXN-20260115-275537', NULL, 3000.00, 2000.00, 3, 0, 37, 'COMPTE_CBDE748D', 34, 30),
('TXN-20260115-738465', '2026-01-15 08:50:09.403626', '2026-01-15 07:50:37.852815', '2026-01-15 07:51:02.059039', NULL, NULL, NULL, 2000.00, NULL, 'TXN-20260115-372858', NULL, 1000.00, 3000.00, 3, 1, 37, 'COMPTE_CBDE748D', 34, 30);

-- --------------------------------------------------------

--
-- Structure de la table `transaction_offline`
--

DROP TABLE IF EXISTS `transaction_offline`;
CREATE TABLE IF NOT EXISTS `transaction_offline` (
  `id_offline` varchar(50) NOT NULL,
  `date_synchro` datetime(6) DEFAULT NULL,
  `date_transaction` datetime(6) NOT NULL,
  `description` tinytext,
  `erreur_synchro` tinytext,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `montant` decimal(15,2) NOT NULL,
  `signature_client` tinytext,
  `statut_synchro` tinyint DEFAULT NULL,
  `type_transaction` tinyint NOT NULL,
  `code_client` bigint NOT NULL,
  `id_compte` varchar(50) NOT NULL,
  `id_employe` int NOT NULL,
  `id_transaction_finale` varchar(50) DEFAULT NULL,
  `id_caissier_validation` int DEFAULT NULL,
  PRIMARY KEY (`id_offline`),
  UNIQUE KEY `UK7ct06rhwqw4jdyjxobm30mv5j` (`id_transaction_finale`),
  KEY `FKliaotchcpy36vyjn7e0cxsvde` (`code_client`),
  KEY `FKpybnb1ixm4qor8ditohfhupjf` (`id_compte`),
  KEY `FKgq9f7igpjeaev1qmpip4b66` (`id_employe`),
  KEY `FKelm2sdwbu0alj571b3x5no1sx` (`id_caissier_validation`)
) ;

--
-- Déchargement des données de la table `transaction_offline`
--

INSERT INTO `transaction_offline` (`id_offline`, `date_synchro`, `date_transaction`, `description`, `erreur_synchro`, `latitude`, `longitude`, `montant`, `signature_client`, `statut_synchro`, `type_transaction`, `code_client`, `id_compte`, `id_employe`, `id_transaction_finale`, `id_caissier_validation`) VALUES
('OFF-565E56CA', '2026-01-15 07:36:55.111247', '2026-01-15 06:30:56.028161', 'retrait ', NULL, NULL, NULL, 100.00, NULL, 3, 1, 1, 'CPT001', 34, 'TXN-20260115-404948', 37),
('OFF-6F232377', '2026-01-15 07:50:37.857091', '2026-01-15 08:50:09.403626', 'retrait ', NULL, NULL, NULL, 2000.00, NULL, 3, 1, 165, 'COMPTE_CBDE748D', 34, 'TXN-20260115-738465', 37),
('OFF-7623BD79', NULL, '2026-01-15 05:08:16.437854', 'retrait ', NULL, NULL, NULL, 200.00, NULL, 0, 1, 1, 'CPT001', 34, NULL, NULL),
('OFF-834FBAF6', NULL, '2026-01-15 06:12:55.318020', 'dépôt ', NULL, NULL, NULL, 1000.00, NULL, 0, 0, 1, 'CPT001', 34, NULL, 37),
('OFF-951A54EF', '2026-01-15 07:48:14.594809', '2026-01-15 08:44:41.288041', 'dépôt ', NULL, NULL, NULL, 1000.00, NULL, 3, 0, 165, 'COMPTE_CBDE748D', 34, 'TXN-20260115-617945', 37),
('OFF-C7197C02', NULL, '2026-01-15 06:41:24.190532', 'retrait ', NULL, NULL, NULL, 50.00, NULL, 2, 1, 1, 'CPT001', 34, NULL, 37),
('OFF001', NULL, '2025-12-27 15:06:46.235112', NULL, NULL, NULL, NULL, 50.00, NULL, 0, 1, 1, 'CPT001', 2, NULL, NULL);

-- --------------------------------------------------------

--
-- Structure de la table `type_compte`
--

DROP TABLE IF EXISTS `type_compte`;
CREATE TABLE IF NOT EXISTS `type_compte` (
  `id_type` int NOT NULL AUTO_INCREMENT,
  `autoriser_retrait` bit(1) DEFAULT NULL,
  `code` varchar(20) NOT NULL,
  `description` tinytext,
  `duree_blocage_jours` int DEFAULT NULL,
  `frais_cloture` decimal(15,2) DEFAULT NULL,
  `frais_ouverture` decimal(15,2) DEFAULT NULL,
  `nom` varchar(50) NOT NULL,
  `solde_minimum` decimal(15,2) DEFAULT NULL,
  `taux_interet` decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (`id_type`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `type_compte`
--

INSERT INTO `type_compte` (`id_type`, `autoriser_retrait`, `code`, `description`, `duree_blocage_jours`, `frais_cloture`, `frais_ouverture`, `nom`, `solde_minimum`, `taux_interet`) VALUES
(1, b'1', 'EPARGNE', NULL, NULL, 2000.00, 1000.00, 'Compte Épargne', 100.00, 2.50),
(2, b'1', 'COURANT', NULL, NULL, NULL, NULL, 'Compte Courant', 0.00, 0.00);

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
CREATE TABLE IF NOT EXISTS `utilisateur` (
  `login` varchar(50) NOT NULL,
  `date_creation` datetime(6) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `statut` enum('ACTIF','INACTIF','SUSPENDU') NOT NULL,
  `telephone` varchar(20) NOT NULL,
  `id_role` int NOT NULL,
  `photo_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`login`),
  KEY `FKr6x7g9mw0va8oe9drohepvywu` (`id_role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`login`, `date_creation`, `email`, `nom`, `password`, `prenom`, `statut`, `telephone`, `id_role`, `photo_path`) VALUES
('admin', NULL, 'admin@savings.local', 'Admin', 'admin123', 'Principal', 'ACTIF', '+2250100000000', 6, NULL),
('brawn1', '2026-01-10 17:42:56.969711', 'brawn@gmail.com', 'SHAWN', '$2a$10$yDoLoOaStYUBTr25cIduWu1Y4CcWt3A6npJQTYXT2UVi.15w96LVa', 'brawn', 'ACTIF', '56415611', 3, NULL),
('cas', '2026-01-03 08:30:10.545965', 'caissier@gmail.com', 'cassier', 'motdepasse123', 'bros', 'ACTIF', '012445467s9r', 4, NULL),
('CLI_fizz_971977', '2026-01-15 02:09:32.064197', 'fizz@gmail.com', 'Fizz', '$2a$10$HY03G9cEFoZXjCS6.TM6rOH/PFCYEzoDLEefVcDRD1eLCw5fGXF2e', 'Jo', 'ACTIF', '698457878', 2, NULL),
('client_7_01', '2026-01-10 15:00:53.864442', 'kamga@test.com', 'Kamga', 'Password123', 'Samuel', 'ACTIF', '670000101', 3, NULL),
('client_7_02', '2026-01-10 15:00:54.064263', 'ngo@test.com', 'Ngo', 'Password123', 'Therese', 'ACTIF', '670000102', 3, NULL),
('client_7_03', '2026-01-10 15:00:54.104897', 'talla@test.com', 'Talla', 'Password123', 'Marc', 'ACTIF', '670000103', 3, NULL),
('client_7_04', '2026-01-10 15:00:54.141907', 'foning@test.com', 'Foning', 'Password123', 'Lea', 'ACTIF', '670000104', 3, NULL),
('client_7_05', '2026-01-10 15:00:54.172741', 'ewane@test.com', 'Ewane', 'Password123', 'Paul', 'ACTIF', '670000105', 3, NULL),
('client_7_06', '2026-01-10 15:00:54.205986', 'etundi@test.com', 'Etundi', 'Password123', 'John', 'ACTIF', '670000106', 3, NULL),
('client_7_07', '2026-01-10 15:00:54.237519', 'moussa@test.com', 'Moussa', 'Password123', 'Aicha', 'ACTIF', '670000107', 3, NULL),
('client_7_08', '2026-01-10 15:00:54.273187', 'bekono@test.com', 'Bekono', 'Password123', 'Guy', 'ACTIF', '670000108', 3, NULL),
('client_7_09', '2026-01-10 15:00:54.307569', 'abena@test.com', 'Abena', 'Password123', 'Serge', 'ACTIF', '670000109', 3, NULL),
('client_7_10', '2026-01-10 15:00:54.354515', 'tchuente@test.com', 'Tchuente', 'Password123', 'Marie', 'ACTIF', '670000110', 3, NULL),
('client_9_01', '2026-01-10 15:00:54.426727', 'zambo@test.com', 'Zambo', 'Password123', 'Eric', 'ACTIF', '690000201', 3, NULL),
('client_9_02', '2026-01-10 15:00:54.456580', 'atangana@test.com', 'Atangana', 'Password123', 'Luc', 'ACTIF', '690000202', 3, NULL),
('client_9_03', '2026-01-10 15:00:54.480174', 'simba@test.com', 'Simba', 'Password123', 'Pierre', 'ACTIF', '690000203', 3, NULL),
('client_9_04', '2026-01-10 15:00:54.507582', 'biloa@test.com', 'Biloa', 'Password123', 'Solange', 'ACTIF', '690000204', 3, NULL),
('client_9_05', '2026-01-10 15:00:54.540739', 'nlate@test.com', 'Nlate', 'Password123', 'Brice', 'ACTIF', '690000205', 3, NULL),
('client_9_06', '2026-01-10 15:00:54.575658', 'owona@test.com', 'Owona', 'Password123', 'Marc', 'ACTIF', '690000206', 3, NULL),
('client_9_07', '2026-01-10 15:00:54.625759', 'mballa@test.com', 'Mballa', 'Password123', 'Rose', 'ACTIF', '690000207', 3, NULL),
('client_9_08', '2026-01-10 15:00:54.660346', 'njikam@test.com', 'Njikam', 'Password123', 'Oumar', 'ACTIF', '690000208', 3, NULL),
('client_9_09', '2026-01-10 15:00:54.692665', 'belibi@test.com', 'Belibi', 'Password123', 'Jean', 'ACTIF', '690000209', 3, NULL),
('client_9_10', '2026-01-10 15:00:54.742110', 'amougou@test.com', 'Amougou', 'Password123', 'Ines', 'ACTIF', '690000210', 3, NULL),
('client1', '2025-12-27 15:06:45.746239', 'client1@example.com', 'Diallo', 'password123', 'Fatou', 'ACTIF', '+221 77 234 56 78', 2, NULL),
('collecteur1', '2025-12-27 15:06:45.746239', 'collecteur1@example.com', 'Sarriel', 'password123', 'Mamadou', 'ACTIF', '+221 77 345 67 89', 3, NULL),
('fo1', '2025-12-29 13:31:04.889331', 'tamofotso90@gmail.com', 'Does', 'motdepasse123', 'Johns', 'ACTIF', '012445467s9r', 2, NULL),
('Fotso', '2026-01-03 12:34:58.946104', 'fotso@gmail.com', 'Fotso', 'fo1234', 'Darrylos', 'ACTIF', '67584648', 2, NULL),
('free', '2026-01-03 14:02:06.965427', 'free@gmail.com', 'don', 'free123', 'free', 'ACTIF', '16465465', 2, NULL),
('frees', '2026-01-14 17:16:45.172702', 'joe@gmail.com', 'joe', '$2a$10$SBeLH2Ar/wKrZAmEr542Le4IW4O26QYpIjt.YkZ5WwPHhBmtdPREq', 'martin', 'ACTIF', '0143456789', 3, NULL),
('fro1', '2026-01-15 04:53:18.272087', 'fro@gmail.com', 'cybor', '$2a$10$XxeVnWZ/gS7dTasdqMHVheSbGwP015Y8V1xBziGlzp.gaxrX7RfMG', 'fro', 'ACTIF', '65485454', 4, NULL),
('jean25', '2026-01-04 11:37:11.721955', 'jean@test.com', 'Jean', 'Password123', 'Dupont', 'ACTIF', '677000000', 3, NULL),
('jean30', '2026-01-04 11:55:28.498377', 'jean30@savely.com', 'Client', 'Password123', 'Nouveau', 'ACTIF', '00000000', 3, NULL),
('jo1', '2026-01-03 15:46:02.134580', 'jo@gmail.com', 'franck', 'jo12345', 'jo', 'ACTIF', '45678654', 3, NULL),
('log', '2026-01-04 09:33:07.254084', 'logs@gmail.com', 'loggos', 'motdepasse123', 'bros', 'ACTIF', '012445467s9r', 2, NULL),
('marie85', '2026-01-04 11:37:29.341515', 'marie@test.com', 'Marie', 'Password123', 'Sali', 'ACTIF', '699000000', 3, NULL),
('soh', '2025-12-27 17:48:38.710453', 'soh.doqe@example.com', 'DoesE', 'motdepasse123', 'Johns', 'ACTIF', '012445D67s9', 3, NULL),
('superviseur1', '2025-12-27 15:06:45.746239', 'superviseur1@example.com', 'Dupont', 'password123', 'Jean', 'ACTIF', '+221 77 123 45 67', 1, NULL),
('testuser', '2026-01-05 11:18:43.736138', 'test@example.com', 'Test', '$2a$10$WxnkkrHS0IlMIw2rjR3e5eC8E21IdREgoWMmbAvi/fhwwGwrhZzh6', 'User', 'ACTIF', '0123456789', 1, NULL),
('toto1', '2026-01-03 14:04:35.132130', 'toto@gmail.com', 'toto', 'toto1234', 'titi', 'ACTIF', '451256512', 3, NULL);

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `client`
--
ALTER TABLE `client`
  ADD CONSTRAINT `FK2pps5fl79e1lmmw33jsll2y38` FOREIGN KEY (`collecteur_assigne`) REFERENCES `employe` (`id_employe`),
  ADD CONSTRAINT `FKkgexke2lkg3db7b32pj0rbd4k` FOREIGN KEY (`login`) REFERENCES `utilisateur` (`login`);

--
-- Contraintes pour la table `clients`
--
ALTER TABLE `clients`
  ADD CONSTRAINT `FK3r8qetq5twytljpg9nf4y44t9` FOREIGN KEY (`collector_id`) REFERENCES `utilisateur` (`login`),
  ADD CONSTRAINT `FKktfm0m7y3yje2f1okl6opmwv5` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`);

--
-- Contraintes pour la table `compte`
--
ALTER TABLE `compte`
  ADD CONSTRAINT `FK4j39u1ymy78ien5aes05ut81e` FOREIGN KEY (`code_client`) REFERENCES `client` (`numero_client`),
  ADD CONSTRAINT `FKf4tk0h14gnm9ffdsu4ep1548m` FOREIGN KEY (`id_type`) REFERENCES `type_compte` (`id_type`);

--
-- Contraintes pour la table `compte_cotisation`
--
ALTER TABLE `compte_cotisation`
  ADD CONSTRAINT `FKcp4gtgdb4ugty50jnq7317qta` FOREIGN KEY (`id_compte`) REFERENCES `compte` (`id_compte`),
  ADD CONSTRAINT `FKcpd1wsef5d4y7hpgxgnax4q0b` FOREIGN KEY (`id_plan`) REFERENCES `plan_cotisation` (`id_plan`);

--
-- Contraintes pour la table `demande_ouverture_compte`
--
ALTER TABLE `demande_ouverture_compte`
  ADD CONSTRAINT `FKeeydx45m0vw8la073l76aied2` FOREIGN KEY (`id_type_compte`) REFERENCES `type_compte` (`id_type`),
  ADD CONSTRAINT `FKivlxs4amr2qdicx1jty0k4q0n` FOREIGN KEY (`id_superviseur_validateur`) REFERENCES `employe` (`id_employe`),
  ADD CONSTRAINT `FKsh6w6motnflh68gas4776c73d` FOREIGN KEY (`code_client`) REFERENCES `client` (`numero_client`);

--
-- Contraintes pour la table `device_token`
--
ALTER TABLE `device_token`
  ADD CONSTRAINT `FKcwpv15umrnt1rch4yvm2d0y1b` FOREIGN KEY (`login_utilisateur`) REFERENCES `utilisateur` (`login`);

--
-- Contraintes pour la table `employe`
--
ALTER TABLE `employe`
  ADD CONSTRAINT `FKayofc6vrp428ew0udu84sftma` FOREIGN KEY (`superviseur_id`) REFERENCES `employe` (`id_employe`),
  ADD CONSTRAINT `FKfbw7jtnfuh8nqyher3hritfml` FOREIGN KEY (`id_agence`) REFERENCES `agence_zone` (`id_agence`),
  ADD CONSTRAINT `FKlia20e1sb30aasmih8bxersej` FOREIGN KEY (`login`) REFERENCES `utilisateur` (`login`);

--
-- Contraintes pour la table `notification`
--
ALTER TABLE `notification`
  ADD CONSTRAINT `FKp94qdnc6smrkgd99t0pe954y0` FOREIGN KEY (`id_transaction`) REFERENCES `transaction` (`id_transaction`);

--
-- Contraintes pour la table `recu`
--
ALTER TABLE `recu`
  ADD CONSTRAINT `FKi5ibdbf0yx68v0xrinrmca13b` FOREIGN KEY (`id_transaction`) REFERENCES `transaction` (`id_transaction`);

--
-- Contraintes pour la table `transaction`
--
ALTER TABLE `transaction`
  ADD CONSTRAINT `FK41hs9ufmm78trxw62c7s0q9xw` FOREIGN KEY (`id_compte`) REFERENCES `compte` (`id_compte`),
  ADD CONSTRAINT `FK6l6bn7a4q4wjqp9t582c3cgdg` FOREIGN KEY (`id_employe_initiateur`) REFERENCES `employe` (`id_employe`),
  ADD CONSTRAINT `FKc5holi1ndwvm8m1cjm6ns8f0x` FOREIGN KEY (`id_caissier_validateur`) REFERENCES `employe` (`id_employe`),
  ADD CONSTRAINT `FKh00iqnsfe2nc413jrp6m9j4rx` FOREIGN KEY (`id_superviseur_validateur`) REFERENCES `employe` (`id_employe`);

--
-- Contraintes pour la table `transaction_offline`
--
ALTER TABLE `transaction_offline`
  ADD CONSTRAINT `FK3g9wp0sh76y9tvf2inn3t8e51` FOREIGN KEY (`id_transaction_finale`) REFERENCES `transaction` (`id_transaction`),
  ADD CONSTRAINT `FKelm2sdwbu0alj571b3x5no1sx` FOREIGN KEY (`id_caissier_validation`) REFERENCES `employe` (`id_employe`),
  ADD CONSTRAINT `FKgq9f7igpjeaev1qmpip4b66` FOREIGN KEY (`id_employe`) REFERENCES `employe` (`id_employe`),
  ADD CONSTRAINT `FKliaotchcpy36vyjn7e0cxsvde` FOREIGN KEY (`code_client`) REFERENCES `client` (`numero_client`),
  ADD CONSTRAINT `FKpybnb1ixm4qor8ditohfhupjf` FOREIGN KEY (`id_compte`) REFERENCES `compte` (`id_compte`);

--
-- Contraintes pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD CONSTRAINT `FKr6x7g9mw0va8oe9drohepvywu` FOREIGN KEY (`id_role`) REFERENCES `role` (`id_role`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
