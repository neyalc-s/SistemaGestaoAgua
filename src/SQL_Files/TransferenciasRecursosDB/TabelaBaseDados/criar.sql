SET ECHO OFF;

BEGIN EXECUTE IMMEDIATE 'DROP TABLE TRANSFER_ASSOC_COTA CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE FAMILIA_DOA_REC_TRANSFER CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Transferencia_Cota CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Alerta_Qualidade_Agua CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Medicao_Qualidade_Agua CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE REC_APLICA_MED_PROT CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Medida_Proteccao CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Medida_Prot_Responsavel CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Parametro_Qualidade CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Recurso_Hidrico CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

CREATE TABLE Recurso_Hidrico (
    codigo_rh           NUMBER(7) NOT NULL,
    tipo_rh             VARCHAR2(60) NULL,
    localizacao_rh      VARCHAR2(200) NULL,
    volume_rh           NUMBER(12,2) NULL,
    sazonalidade_rh     VARCHAR2(60) NULL,
    vulnerabilidade_rh  VARCHAR2(200) NULL,
    nivel_exploracao_rh VARCHAR2(60) NULL
);
ALTER TABLE Recurso_Hidrico ADD CONSTRAINT pk_recurso_hidrico PRIMARY KEY (codigo_rh);
ALTER TABLE Recurso_Hidrico
    ADD CONSTRAINT ck_recurso_tipo CHECK (tipo_rh IN ('Subterranea', 'Superficial', 'Pluvial'));
ALTER TABLE Recurso_Hidrico
    ADD CONSTRAINT ck_recurso_nivel_exploracao
    CHECK (nivel_exploracao_rh IN ('Baixo', 'Medio', 'Alto', 'Critico'));

CREATE TABLE Parametro_Qualidade (
    codigo_parametro NUMBER(7) NOT NULL,
    nome_parametro   VARCHAR2(60) NOT NULL,
    unidade_padrao   VARCHAR2(20) NULL
);
ALTER TABLE Parametro_Qualidade ADD CONSTRAINT pk_parametro_qualidade PRIMARY KEY (codigo_parametro);

CREATE TABLE Medida_Prot_Responsavel (
    cod_responsavel  NUMBER(7) NOT NULL,
    nome_responsavel VARCHAR2(60) NOT NULL
);
ALTER TABLE Medida_Prot_Responsavel ADD CONSTRAINT pk_medida_prot_responsavel PRIMARY KEY (cod_responsavel);

CREATE TABLE Medida_Proteccao (
    cod_medida_proteccao NUMBER(7) NOT NULL,
    cod_responsavel      NUMBER(7) NOT NULL,
    descricao_medida     VARCHAR2(100) NULL
);
ALTER TABLE Medida_Proteccao ADD CONSTRAINT pk_medida_proteccao PRIMARY KEY (cod_medida_proteccao);
ALTER TABLE Medida_Proteccao
    ADD CONSTRAINT fk_med_prot_responsavel FOREIGN KEY (cod_responsavel)
    REFERENCES Medida_Prot_Responsavel (cod_responsavel);

CREATE TABLE REC_APLICA_MED_PROT (
    cod_medida_proteccao NUMBER(7) NOT NULL,
    codigo_rh            NUMBER(7) NOT NULL,
    data_impl            DATE NULL
);
ALTER TABLE REC_APLICA_MED_PROT ADD CONSTRAINT pk_rec_aplica_med_prot PRIMARY KEY (cod_medida_proteccao, codigo_rh);
ALTER TABLE REC_APLICA_MED_PROT
    ADD CONSTRAINT fk_rec_aplica_med FOREIGN KEY (cod_medida_proteccao)
    REFERENCES Medida_Proteccao (cod_medida_proteccao);
ALTER TABLE REC_APLICA_MED_PROT
    ADD CONSTRAINT fk_rec_aplica_rh FOREIGN KEY (codigo_rh)
    REFERENCES Recurso_Hidrico (codigo_rh);

CREATE TABLE Medicao_Qualidade_Agua (
    cod_qualidade_agua NUMBER(7) NOT NULL,
    codigo_rh          NUMBER(7) NOT NULL,
    codigo_parametro   NUMBER(7) NOT NULL,
    equipe_id          NUMBER(7) NOT NULL,
    valor              NUMBER(8,2) NOT NULL,
    data_medicao       DATE NOT NULL
);
ALTER TABLE Medicao_Qualidade_Agua ADD CONSTRAINT pk_medicao_qualidade_agua PRIMARY KEY (cod_qualidade_agua);
ALTER TABLE Medicao_Qualidade_Agua
    ADD CONSTRAINT fk_med_qual_recurso FOREIGN KEY (codigo_rh)
    REFERENCES Recurso_Hidrico (codigo_rh);
ALTER TABLE Medicao_Qualidade_Agua
    ADD CONSTRAINT fk_med_qual_parametro FOREIGN KEY (codigo_parametro)
    REFERENCES Parametro_Qualidade (codigo_parametro);

CREATE TABLE Alerta_Qualidade_Agua (
    codigo_alerta   NUMBER(7) NOT NULL,
    codigo_medicao  NUMBER(7) NOT NULL,
    codigo_rh       NUMBER(7) NOT NULL,
    mensagem_alerta VARCHAR2(1000) NOT NULL,
    data_alerta     TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL
);
ALTER TABLE Alerta_Qualidade_Agua
    ADD CONSTRAINT pk_alerta_qualidade_agua PRIMARY KEY (codigo_alerta);
ALTER TABLE Alerta_Qualidade_Agua
    ADD CONSTRAINT fk_alerta_medicao FOREIGN KEY (codigo_medicao)
    REFERENCES Medicao_Qualidade_Agua (cod_qualidade_agua);
ALTER TABLE Alerta_Qualidade_Agua
    ADD CONSTRAINT fk_alerta_recurso FOREIGN KEY (codigo_rh)
    REFERENCES Recurso_Hidrico (codigo_rh);
CREATE UNIQUE INDEX uk_alerta_medicao
    ON Alerta_Qualidade_Agua (codigo_medicao);

CREATE TABLE Transferencia_Cota (
    codigo_tc                 NUMBER(7) NOT NULL,
    cod_fam_doadora_tc        NUMBER(7) NOT NULL,
    cod_fam_receptora_tc      NUMBER(7) NOT NULL,
    volume_cedido_tc          NUMBER(8,2) NOT NULL,
    motivo_solicitacao_tc     VARCHAR2(200) NULL,
    data_aprovacao_tc         DATE NULL,
    validade_transferencia_tc DATE NULL
);
ALTER TABLE Transferencia_Cota ADD CONSTRAINT pk_transferencia_cota PRIMARY KEY (codigo_tc);

CREATE TABLE FAMILIA_DOA_REC_TRANSFER (
    codigo_tc         NUMBER(7) NOT NULL,
    codigo_fb         NUMBER(7) NOT NULL,
    tipo_participacao VARCHAR2(30) NULL
);
ALTER TABLE FAMILIA_DOA_REC_TRANSFER ADD CONSTRAINT pk_fam_doa_rec_transfer PRIMARY KEY (codigo_tc, codigo_fb);
ALTER TABLE FAMILIA_DOA_REC_TRANSFER
    ADD CONSTRAINT ck_fdr_tipo_participacao CHECK (tipo_participacao IN ('Doa', 'Recebe'));
ALTER TABLE FAMILIA_DOA_REC_TRANSFER
    ADD CONSTRAINT fk_fdr_transferencia FOREIGN KEY (codigo_tc)
    REFERENCES Transferencia_Cota (codigo_tc);

CREATE TABLE TRANSFER_ASSOC_COTA (
    codigo_tc   NUMBER(7) NOT NULL,
    codigo_cota NUMBER(7) NOT NULL,
    codigo_fb   NUMBER(7) NOT NULL
);
ALTER TABLE TRANSFER_ASSOC_COTA ADD CONSTRAINT pk_transfer_assoc_cota PRIMARY KEY (codigo_tc, codigo_cota, codigo_fb);
ALTER TABLE TRANSFER_ASSOC_COTA
    ADD CONSTRAINT fk_tac_transferencia FOREIGN KEY (codigo_tc)
    REFERENCES Transferencia_Cota (codigo_tc);
