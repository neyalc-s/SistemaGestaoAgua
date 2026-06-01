SET ECHO OFF;

CREATE OR REPLACE TRIGGER trg_ponto_distribuicao_bi
BEFORE INSERT ON Ponto_Distribuicao
FOR EACH ROW
BEGIN
    IF :NEW.codigo_pd IS NULL THEN
        SELECT seq_ponto_distribuicao.NEXTVAL INTO :NEW.codigo_pd FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_registro_consumo_bi
BEFORE INSERT ON Registro_Consumo
FOR EACH ROW
BEGIN
    IF :NEW.codigo_rc IS NULL THEN
        SELECT seq_registro_consumo.NEXTVAL INTO :NEW.codigo_rc FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_historico_manutencao_bi
BEFORE INSERT ON Historico_Manutencao
FOR EACH ROW
BEGIN
    IF :NEW.cod_historico_manutencao IS NULL THEN
        SELECT seq_historico_manutencao.NEXTVAL INTO :NEW.cod_historico_manutencao FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_historico_abastecimento_bi
BEFORE INSERT ON Historico_Abastecimento
FOR EACH ROW
BEGIN
    IF :NEW.cod_abastecimento IS NULL THEN
        SELECT seq_historico_abastecimento.NEXTVAL INTO :NEW.cod_abastecimento FROM DUAL;
    END IF;
END;
/
