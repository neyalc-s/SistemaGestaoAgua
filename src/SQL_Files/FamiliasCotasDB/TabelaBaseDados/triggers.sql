SET ECHO OFF;

CREATE OR REPLACE TRIGGER trg_localizacao_bi
BEFORE INSERT ON Localizacao
FOR EACH ROW
BEGIN
    IF :NEW.cod_localizacao IS NULL THEN
        SELECT seq_localizacao.NEXTVAL INTO :NEW.cod_localizacao FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_necessidade_bi
BEFORE INSERT ON Necessidade
FOR EACH ROW
BEGIN
    IF :NEW.cod_necessidade IS NULL THEN
        SELECT seq_necessidade.NEXTVAL INTO :NEW.cod_necessidade FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_familia_beneficiaria_bi
BEFORE INSERT ON Familia_Beneficiaria
FOR EACH ROW
BEGIN
    IF :NEW.codigo_fb IS NULL THEN
        SELECT seq_familia_beneficiaria.NEXTVAL INTO :NEW.codigo_fb FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_cota_agua_bi
BEFORE INSERT ON Cota_Agua
FOR EACH ROW
BEGIN
    IF :NEW.codigo_cota IS NULL THEN
        SELECT seq_cota_agua.NEXTVAL INTO :NEW.codigo_cota FROM DUAL;
    END IF;
END;
/
