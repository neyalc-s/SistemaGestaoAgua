SET ECHO OFF;

CREATE OR REPLACE TRIGGER trg_comite_bi
BEFORE INSERT ON Comite
FOR EACH ROW
BEGIN
    IF :NEW.cod_comite_responsavel IS NULL THEN
        SELECT seq_comite.NEXTVAL INTO :NEW.cod_comite_responsavel FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_equipe_tecnica_bi
BEFORE INSERT ON Equipe_Tecnica
FOR EACH ROW
BEGIN
    IF :NEW.equipe_id IS NULL THEN
        SELECT seq_equipe_tecnica.NEXTVAL INTO :NEW.equipe_id FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_disjuncao_tec_manut
BEFORE INSERT OR UPDATE ON Tecnico_Manutencao
FOR EACH ROW
DECLARE
    v_result NUMBER;
BEGIN
    v_result := verifica_disjuncao_equipe(:NEW.equipe_id);
END;
/

CREATE OR REPLACE TRIGGER trg_disjuncao_anal_qual
BEFORE INSERT OR UPDATE ON Analista_Qualidade
FOR EACH ROW
DECLARE
    v_result NUMBER;
BEGIN
    v_result := verifica_disjuncao_equipe(:NEW.equipe_id);
END;
/

CREATE OR REPLACE TRIGGER trg_disjuncao_educ_com
BEFORE INSERT OR UPDATE ON Educador_Comunitario
FOR EACH ROW
DECLARE
    v_result NUMBER;
BEGIN
    v_result := verifica_disjuncao_equipe(:NEW.equipe_id);
END;
/

CREATE OR REPLACE TRIGGER trg_ferramenta_manutencao_bi
BEFORE INSERT ON Ferramenta_Manutencao
FOR EACH ROW
BEGIN
    IF :NEW.cod_ferramenta_disponivel IS NULL THEN
        SELECT seq_ferramenta_manutencao.NEXTVAL INTO :NEW.cod_ferramenta_disponivel FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_equipamento_analista_bi
BEFORE INSERT ON Equipamento_Analista
FOR EACH ROW
BEGIN
    IF :NEW.cod_equipamento IS NULL THEN
        SELECT seq_equipamento_analista.NEXTVAL INTO :NEW.cod_equipamento FROM DUAL;
    END IF;
END;
/

CREATE OR REPLACE TRIGGER trg_material_educador_bi
BEFORE INSERT ON Material_Educador
FOR EACH ROW
BEGIN
    IF :NEW.cod_material IS NULL THEN
        SELECT seq_material_educador.NEXTVAL INTO :NEW.cod_material FROM DUAL;
    END IF;
END;
/
