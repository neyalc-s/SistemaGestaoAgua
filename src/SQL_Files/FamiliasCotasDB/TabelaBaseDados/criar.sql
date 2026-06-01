SET ECHO OFF;

BEGIN EXECUTE IMMEDIATE 'DROP TABLE NECESS_ASSOC_FAMILIA CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Cota_Agua CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Familia_Beneficiaria CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Necessidade CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Localizacao CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

CREATE TABLE Localizacao (
    cod_localizacao       NUMBER(7) NOT NULL,
    aldeia                VARCHAR2(60) NOT NULL,
    coordenadas_gps       VARCHAR2(60) NULL
);
ALTER TABLE Localizacao ADD CONSTRAINT pk_localizacao PRIMARY KEY (cod_localizacao);

CREATE TABLE Necessidade (
    cod_necessidade       NUMBER(7) NOT NULL,
    descricao_necessidade VARCHAR2(60) NOT NULL
);
ALTER TABLE Necessidade ADD CONSTRAINT pk_necessidade PRIMARY KEY (cod_necessidade);

CREATE TABLE Familia_Beneficiaria (
    codigo_fb                 NUMBER(7) NOT NULL,
    codigo_pd                 NUMBER(7) NOT NULL,
    cod_localizacao           NUMBER(7) NULL,
    nome_responsavel_fb       VARCHAR2(60) NOT NULL,
    num_membros_fb            NUMBER(2) NULL,
    perfil_socioeconomico_fb  VARCHAR2(60) NULL,
    contacto_fb               VARCHAR2(20) NULL,
    estado_fb                 VARCHAR2(20) DEFAULT 'Activo' NOT NULL
);
ALTER TABLE Familia_Beneficiaria ADD CONSTRAINT pk_familia_beneficiaria PRIMARY KEY (codigo_fb);
ALTER TABLE Familia_Beneficiaria
    ADD CONSTRAINT ck_familia_estado CHECK (estado_fb IN ('Activo', 'Inactivo'));
ALTER TABLE Familia_Beneficiaria
    ADD CONSTRAINT ck_familia_perfil_socio
    CHECK (perfil_socioeconomico_fb IN ('Baixa renda', 'Media renda', 'Alta renda'));
ALTER TABLE Familia_Beneficiaria
    ADD CONSTRAINT fk_fam_benef_localizacao FOREIGN KEY (cod_localizacao)
    REFERENCES Localizacao (cod_localizacao);

CREATE TABLE Cota_Agua (
    codigo_cota                 NUMBER(7) NOT NULL,
    codigo_fb                   NUMBER(7) NOT NULL,
    volume_semanal_ca           NUMBER(8,2) NOT NULL,
    periodo_validade_ca         DATE NULL,
    ajuste_sazonal_ca           VARCHAR2(60) NULL,
    transferencia_autorizada_ca VARCHAR2(3) NULL,
    saldo_disponivel_ca         NUMBER(8,2) NULL
);
ALTER TABLE Cota_Agua ADD CONSTRAINT pk_cota_agua PRIMARY KEY (codigo_cota, codigo_fb);
ALTER TABLE Cota_Agua
    ADD CONSTRAINT ck_cota_ajuste_sazonal
    CHECK (
        ajuste_sazonal_ca IN (
            'Verao',
            'Inverno',
            'Chuvoso',
            'Seco',
            'Transicao Seca',
            'Pico Seco',
            'Inicio Chuvoso',
            'Alta Demanda',
            'Fim de Ano',
            'Normal'
        )
    );
ALTER TABLE Cota_Agua
    ADD CONSTRAINT ck_cota_transferencia_aut
    CHECK (transferencia_autorizada_ca IN ('SIM', 'NAO'));
ALTER TABLE Cota_Agua
    ADD CONSTRAINT fk_cota_agua_fam_benef FOREIGN KEY (codigo_fb)
    REFERENCES Familia_Beneficiaria (codigo_fb);

CREATE TABLE NECESS_ASSOC_FAMILIA (
    cod_necessidade NUMBER(7) NOT NULL,
    codigo_fb       NUMBER(7) NOT NULL
);
ALTER TABLE NECESS_ASSOC_FAMILIA ADD CONSTRAINT pk_necess_assoc_familia PRIMARY KEY (cod_necessidade, codigo_fb);
ALTER TABLE NECESS_ASSOC_FAMILIA
    ADD CONSTRAINT fk_nec_assoc_necessidade FOREIGN KEY (cod_necessidade)
    REFERENCES Necessidade (cod_necessidade);
ALTER TABLE NECESS_ASSOC_FAMILIA
    ADD CONSTRAINT fk_nec_assoc_familia FOREIGN KEY (codigo_fb)
    REFERENCES Familia_Beneficiaria (codigo_fb);
