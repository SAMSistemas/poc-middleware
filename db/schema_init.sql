-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `poc_middleware` DEFAULT CHARACTER SET utf8 ;
USE `poc_middleware` ;

-- -----------------------------------------------------
-- Table `mydb`.`Plan`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `poc_middleware`.`Plan` (
  `id` INT NOT NULL,
  `codigo` INT NULL,
  `descripcion` VARCHAR(45) NULL,
  `fecha_desde` DATETIME NULL,
  `fecha_hasta` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `codigo_UNIQUE` (`codigo` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Filial`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `poc_middleware`.`Filial` (
  `codigo` INT NOT NULL,
  `descripcion` VARCHAR(45) NULL,
  PRIMARY KEY (`codigo`),
  UNIQUE INDEX `codigo_UNIQUE` (`codigo` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Localizacion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `poc_middleware`.`Localizacion` (
  `id` INT NOT NULL,
  `calle` VARCHAR(256) NULL,
  `altura` INT NULL,
  `codigo_postal` INT NULL,
  `localidad_descripcion` VARCHAR(256) NULL,
  `geolocalizacion` POINT NULL,
  `pais` VARCHAR(45) NULL,
  `provincia` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`Socio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `poc_middleware`.`Socio` (
  `id` INT NOT NULL,
  `titular` TINYINT NULL DEFAULT 1,
  `mail` VARCHAR(256) NULL,
  `telefono` VARCHAR(45) NULL,
  `id_plan` INT NOT NULL,
  `codigo_filial` INT NOT NULL,
  `id_localizacion` INT NOT NULL,
  PRIMARY KEY (`id`, `id_plan`, `codigo_filial`, `id_localizacion`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_Socio_Plan_idx` (`id_plan` ASC),
  INDEX `fk_Socio_Filial1_idx` (`codigo_filial` ASC),
  INDEX `fk_Socio_Localizacion1_idx` (`id_localizacion` ASC),
  CONSTRAINT `fk_Socio_Plan`
    FOREIGN KEY (`id_plan`)
    REFERENCES `poc_middleware`.`Plan` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Socio_Filial1`
    FOREIGN KEY (`codigo_filial`)
    REFERENCES `poc_middleware`.`Filial` (`codigo`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Socio_Localizacion1`
    FOREIGN KEY (`id_localizacion`)
    REFERENCES `poc_middleware`.`Localizacion` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`TramiteSocio`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `poc_middleware`.`TramiteSocio` (
  `id` INT NOT NULL,
  `tipo` ENUM('cambioPlan', 'cambioDatosContacto', 'cambioFilial', 'credenciales', 'prestaciones', 'reintegros') NULL,
  `descripcion` VARCHAR(45) NULL,
  `exitoso` TINYINT NULL DEFAULT 0,
  `id_socio` INT NOT NULL,
  PRIMARY KEY (`id`, `id_socio`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_TramiteSocio_Socio1_idx` (`id_socio` ASC),
  CONSTRAINT `fk_TramiteSocio_Socio1`
    FOREIGN KEY (`id_socio`)
    REFERENCES `poc_middleware`.`Socio` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- Insert data to DB

INSERT INTO poc_middleware.Plan(id,codigo, descripcion, fecha_desde, fecha_hasta)
  VALUES (1,210,'Plan 210', now(), null),
  (2,310,'Plan 310', now(), null),
  (3,410,'Plan 410', now(), null),
  (4,450,'Plan 450', now(), null),
  (5,510,'Plan 510', now(), null);

INSERT INTO poc_middleware.Filial(codigo, descripcion)
    VALUES (23, 'Rosario'),
      (10,'Metropolitana'),
      (8, 'Cordoba'),
      (32, 'Tucuman');

INSERT INTO poc_middleware.Localizacion(id, calle, altura, codigo_postal, localidad_descripcion, geolocalizacion, pais, provincia)
  VALUES (1, 'Av. Juan B Justo', 8767, 1408, 'CABA', Point(-45.3213,-56.21344), 'Argentina', 'Buenos Aires'),
    (2, 'Junin', 999, 1897, 'Las Malvinas', Point(-32.926613,-60.677632), 'Argentina', 'Rosario'),
    (3, 'Rincon', 1224, 5000, 'Cordoba', Point(-31.407752,-64.169209), 'Argentina', 'Cordoba'),
    (4, 'Libertad', 273, 4000, 'S.M. de Tucuman', Point(-26.830897,-65.224328), 'Argentina', 'Tucuman');

INSERT INTO poc_middleware.Socio(id, mail, telefono, id_plan, codigo_filial, id_localizacion)
VALUES (1, 'fcatinello@gmail.com', '1137786481', 2, 10, 1),
  (2, 'pepito@gmail.com', '1154632312', 3, 23, 2),
  (3, 'pedro.gonzalez@gmail.com', '1187654532', 1, 32, 4),
  (4, 'gisela_bernal@gmail.com', '1135678798', 5, 8, 3);