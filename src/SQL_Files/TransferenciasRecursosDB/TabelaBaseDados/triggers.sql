SET ECHO OFF;

CREATE OR REPLACE TRIGGER trg_recurso_hidrico_bi
BEFORE INSERT ON Recurso_Hidrico
FOR EACH ROW
BEGIN
    IF :NEW.codigo_rh IS NULL THEN
        SELECT seq_recurso_hidrico.NEXTVAL INTO :NEW.codigo_rh FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_parametro_qualidade_bi
BEFORE INSERT ON Parametro_Qualidade
FOR EACH ROW
BEGIN
    IF :NEW.codigo_parametro IS NULL THEN
        SELECT seq_parametro_qualidade.NEXTVAL INTO :NEW.codigo_parametro FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_medida_prot_responsavel_bi
BEFORE INSERT ON Medida_Prot_Responsavel
FOR EACH ROW
BEGIN
    IF :NEW.cod_responsavel IS NULL THEN
        SELECT seq_medida_prot_responsavel.NEXTVAL INTO :NEW.cod_responsavel FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_medida_proteccao_bi
BEFORE INSERT ON Medida_Proteccao
FOR EACH ROW
BEGIN
    IF :NEW.cod_medida_proteccao IS NULL THEN
        SELECT seq_medida_proteccao.NEXTVAL INTO :NEW.cod_medida_proteccao FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_medicao_qualidade_agua_bi
BEFORE INSERT ON Medicao_Qualidade_Agua
FOR EACH ROW
BEGIN
    IF :NEW.cod_qualidade_agua IS NULL THEN
        SELECT seq_medicao_qualidade_agua.NEXTVAL INTO :NEW.cod_qualidade_agua FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_alerta_qualidade_agua_bi
BEFORE INSERT ON Alerta_Qualidade_Agua
FOR EACH ROW
BEGIN
    IF :NEW.codigo_alerta IS NULL THEN
        SELECT seq_alerta_qualidade_agua.NEXTVAL INTO :NEW.codigo_alerta FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_transferencia_cota_bi
BEFORE INSERT ON Transferencia_Cota
FOR EACH ROW
BEGIN
    IF :NEW.codigo_tc IS NULL THEN
        SELECT seq_transferencia_cota.NEXTVAL INTO :NEW.codigo_tc FROM DUAL;
    END IF;
END;
/
