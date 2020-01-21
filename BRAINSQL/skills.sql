CREATE TABLE `skills` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `skill_id` int(11) NOT NULL DEFAULT '0',
  `character_id` int(11) NOT NULL DEFAULT '0',
  `skill_level` int(11) NOT NULL DEFAULT '0',
  `master_level` int(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `skills_ibfk_1` (`character_id`),
  CONSTRAINT `skills_ibfk_1` FOREIGN KEY (`character_id`) REFERENCES `characters` (`id`) ON DELETE CASCADE,
  CONSTRAINT `skill_id_character_id` UNIQUE (`skill_id`, `character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;