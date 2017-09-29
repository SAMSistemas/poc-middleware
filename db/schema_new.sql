-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema poc_middleware
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema poc_middleware
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `poc_middleware` DEFAULT CHARACTER SET utf8 ;
USE `poc_middleware` ;

-- -----------------------------------------------------
-- Table `poc_middleware`.`Filial`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `poc_middleware`.`Filial` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `codigo` INT(11) NULL DEFAULT NULL,
  `descripcion` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  UNIQUE INDEX `codigo_UNIQUE` (`codigo` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `poc_middleware`.`Localizacion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `poc_middleware`.`Localizacion` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `calle` VARCHAR(256) NULL DEFAULT NULL,
  `altura` INT(11) NULL DEFAULT NULL,
  `codigo_postal` INT(11) NULL DEFAULT NULL,
  `localidad_descripcion` VARCHAR(256) NULL DEFAULT NULL,
  `geolocalizacion` POINT NULL DEFAULT NULL,
  `pais` VARCHAR(45) NULL DEFAULT NULL,
  `provincia` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `poc_middleware`.`Plan`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `poc_middleware`.`Plan` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `codigo` INT(11) NULL DEFAULT NULL,
  `descripcion` VARCHAR(45) NULL DEFAULT NULL,
  `fecha_desde` DATETIME NULL DEFAULT NULL,
  `fecha_hasta` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `codigo_UNIQUE` (`codigo` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `poc_middleware`.`Socio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `poc_middleware`.`Socio` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `contrato` VARCHAR(45) NULL DEFAULT NULL,
  `titular` TINYINT(4) NULL DEFAULT '1',
  `nombre` VARCHAR(256) NULL DEFAULT NULL,
  `mail` VARCHAR(256) NULL DEFAULT NULL,
  `telefono` VARCHAR(45) NULL DEFAULT NULL,
  `id_localizacion` INT(11) NOT NULL,
  `id_plan` INT(11) NOT NULL,
  `id_filial` INT(11) NOT NULL,
  PRIMARY KEY (`id`, `id_localizacion`, `id_plan`, `id_filial`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  UNIQUE INDEX `contrato_UNIQUE` (`contrato` ASC),
  INDEX `fk_Socio_Localizacion1_idx` (`id_localizacion` ASC),
  INDEX `fk_Socio_Plan1_idx` (`id_plan` ASC),
  INDEX `fk_Socio_Filial1_idx` (`id_filial` ASC),
  CONSTRAINT `fk_Socio_Localizacion1`
    FOREIGN KEY (`id_localizacion`)
    REFERENCES `poc_middleware`.`Localizacion` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Socio_Plan1`
    FOREIGN KEY (`id_plan`)
    REFERENCES `poc_middleware`.`Plan` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Socio_Filial1`
    FOREIGN KEY (`id_filial`)
    REFERENCES `poc_middleware`.`Filial` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `poc_middleware`.`TramiteSocio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `poc_middleware`.`TramiteSocio` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `tipo` ENUM('CambioPlan', 'CambioDatosContacto', 'CambioFilial', 'Credencial', 'Prestacion', 'Reintegro') NULL DEFAULT NULL,
  `descripcion` VARCHAR(45) NULL DEFAULT NULL,
  `estado` VARCHAR(45) NULL,
  `timestamp` DATETIME NULL,
  `id_socio` INT(11) NOT NULL,
  PRIMARY KEY (`id`, `id_socio`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_TramiteSocio_Socio_idx` (`id_socio` ASC),
  CONSTRAINT `fk_TramiteSocio_Socio`
    FOREIGN KEY (`id_socio`)
    REFERENCES `poc_middleware`.`Socio` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- Insert data to DB

INSERT INTO poc_middleware.Plan(codigo, descripcion, fecha_desde, fecha_hasta)
  VALUES (210,'Plan 210', now(), null),
  (310,'Plan 310', now(), null),
  (410,'Plan 410', now(), null),
  (450,'Plan 450', now(), null),
  (510,'Plan 510', now(), null);

INSERT INTO poc_middleware.Filial(codigo, descripcion)
    VALUES (23, 'Rosario'),
      (10,'Metropolitana'),
      (8, 'Cordoba'),
      (32, 'Tucuman');

INSERT INTO poc_middleware.Localizacion(calle, altura, codigo_postal, localidad_descripcion, geolocalizacion, pais, provincia)
  VALUES ('Av. Juan B Justo', 8767, 1408, 'CABA', Point(-45.3213,-56.21344), 'Argentina', 'Buenos Aires'),
    ('Junin', 999, 1897, 'Las Malvinas', Point(-32.926613,-60.677632), 'Argentina', 'Rosario'),
    ('Rincon', 1224, 5000, 'Cordoba', Point(-31.407752,-64.169209), 'Argentina', 'Cordoba'),
    ('Libertad', 273, 4000, 'S.M. de Tucuman', Point(-26.830897,-65.224328), 'Argentina', 'Tucuman');

INSERT INTO poc_middleware.Socio(contrato, nombre, mail, telefono, id_plan, id_filial, id_localizacion)
VALUES (62369950701, 'Federico Catinello', 'fcatinello@gmail.com', '1137786481', 2, 2, 1),
  (61345630601, 'Leandro Alessandrello', 'leandro.aless@gmail.com', '1154632312', 3, 1, 2),
  (60256930901, 'Walter Chere', 'walterchere@gmail.com', '1187654532', 1, 4, 4),
  (62564380610, 'Jonatan Salas', 'jonatan.salas.js@gmail.com', '1135678798', 5, 3, 3);    