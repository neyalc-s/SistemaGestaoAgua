SET ECHO OFF;

BEGIN EXECUTE IMMEDIATE 'DROP TABLE Material_Educador CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Equipamento_Analista CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Ferramenta_Manutencao CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Educador_Comunitario CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Analista_Qualidade CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Tecnico_Manutencao CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Equipe_Tecnica CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Comite CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

CREATE TABLE Comite (
    cod_comite_responsavel NUMBER(7) NOT NULL,
    nome_comite            VARCHAR2(60) NOT NULL,
    data_criacao           DATE NULL
);
ALTER TABLE Comite ADD CONSTRAINT pk_comite PRIMARY KEY (cod_comite_responsavel);

CREATE TABLE Equipe_Tecnica (
    equipe_id              NUMBER(7) NOT NULL,
    nome                   VARCHAR2(60) NULL,
    area_actuacao          VARCHAR2(200) NULL,
    nivel_formacao         VARCHAR2(60) NULL,
    contacto               VARCHAR2(20) NULL,
    supervisor_responsavel VARCHAR2(60) NULL
);
ALTER TABLE Equipe_Tecnica ADD CONSTRAINT pk_equipe_tecnica PRIMARY KEY (equipe_id);
ALTER TABLE Equipe_Tecnica
    ADD CONSTRAINT ck_equipe_nivel_formacao
    CHECK (
        nivel_formacao IN (
            'Ensino medio',
            'Tecnico profissional',
            'Licenciatura',
            'Mestrado',
            'Doutoramento',
            'Outro'
        )
    );

CREATE TABLE Tecnico_Manutencao (
    equipe_id            NUMBER(7) NOT NULL,
    habilidade_tecnica   VARCHAR2(60) NULL,
    tempo_medio_resposta NUMBER(3) NULL
);
ALTER TABLE Tecnico_Manutencao ADD CONSTRAINT pk_tecnico_manutencao PRIMARY KEY (equipe_id);
ALTER TABLE Tecnico_Manutencao
    ADD CONSTRAINT fk_tec_manut_equipe FOREIGN KEY (equipe_id)
    REFERENCES Equipe_Tecnica (equipe_id);

CREATE TABLE Analista_Qualidade (
    equipe_id             NUMBER(7) NOT NULL,
    codigo_parametro      NUMBER(7) NOT NULL,
    especialidade_analise VARCHAR2(60) NULL,
    frequencia_amostragem VARCHAR2(20) NULL
);
ALTER TABLE Analista_Qualidade ADD CONSTRAINT pk_analista_qualidade PRIMARY KEY (equipe_id);
ALTER TABLE Analista_Qualidade ADD CONSTRAINT uk_analista_parametro UNIQUE (equipe_id, codigo_parametro);
ALTER TABLE Analista_Qualidade
    ADD CONSTRAINT ck_analista_frequencia CHECK (frequencia_amostragem IN ('Semanal', 'Quinzenal', 'Mensal'));
ALTER TABLE Analista_Qualidade
    ADD CONSTRAINT fk_anal_qual_equipe FOREIGN KEY (equipe_id)
    REFERENCES Equipe_Tecnica (equipe_id);

CREATE TABLE Educador_Comunitario (
    equipe_id                  NUMBER(7) NOT NULL,
    metodologia_sensibilizacao VARCHAR2(200) NOT NULL,
    lingua_local               VARCHAR2(60) NULL,
    comunidade_atendida        VARCHAR2(600) NULL
);
ALTER TABLE Educador_Comunitario ADD CONSTRAINT pk_educador_comunitario PRIMARY KEY (equipe_id);
ALTER TABLE Educador_Comunitario
    ADD CONSTRAINT fk_educ_com_equipe FOREIGN KEY (equipe_id)
    REFERENCES Equipe_Tecnica (equipe_id);

CREATE TABLE Ferramenta_Manutencao (
    cod_ferramenta_disponivel NUMBER(7) NOT NULL,
    equipe_id                 NUMBER(7) NULL,
    nome_ferramenta           VARCHAR2(60) NOT NULL
);
ALTER TABLE Ferramenta_Manutencao ADD CONSTRAINT pk_ferramenta_manutencao PRIMARY KEY (cod_ferramenta_disponivel);
ALTER TABLE Ferramenta_Manutencao
    ADD CONSTRAINT fk_ferr_manut_tecnico FOREIGN KEY (equipe_id)
    REFERENCES Tecnico_Manutencao (equipe_id);

CREATE TABLE Equipamento_Analista (
    cod_equipamento  NUMBER(7) NOT NULL,
    equipe_id         NUMBER(7) NOT NULL,
    nome_equipamento VARCHAR2(60) NOT NULL
);
ALTER TABLE Equipamento_Analista ADD CONSTRAINT pk_equipamento_analista PRIMARY KEY (cod_equipamento);
ALTER TABLE Equipamento_Analista
    ADD CONSTRAINT fk_equip_analista FOREIGN KEY (equipe_id)
    REFERENCES Analista_Qualidade (equipe_id);

CREATE TABLE Material_Educador (
    cod_material  NUMBER(7) NOT NULL,
    equipe_id     NUMBER(7) NOT NULL,
    nome_material VARCHAR2(60) NOT NULL
);
ALTER TABLE Material_Educador ADD CONSTRAINT pk_material_educador PRIMARY KEY (cod_material);
ALTER TABLE Material_Educador
    ADD CONSTRAINT fk_mat_educador FOREIGN KEY (equipe_id)
    REFERENCES Educador_Comunitario (equipe_id);
