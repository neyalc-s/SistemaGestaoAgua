SET ECHO OFF;

BEGIN EXECUTE IMMEDIATE 'DROP TABLE Historico_Abastecimento CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Historico_Manutencao CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Registro_Consumo CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Retirada_Agua_Pendente CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Ponto_Distribuicao CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

CREATE TABLE Ponto_Distribuicao (
    codigo_pd                    NUMBER(7) NOT NULL,
    cod_comite_responsavel       NUMBER(7) NOT NULL,
    equipe_id                    NUMBER(7) NOT NULL,
    codigo_rh                    NUMBER(7) NOT NULL,
    volume_actual_pd             NUMBER(9,2),
    localizacao_pd               VARCHAR2(200) NULL,
    tipo_infraestrutura_pd       VARCHAR2(60) NULL,
    capacidade_armazenamento_pd  NUMBER(9,2) NULL,
    fonte_abastecimento_pd       VARCHAR2(60) NULL,
    tecnologia_tratamento_pd     VARCHAR2(100) NULL,
    data_instalacao_pd           DATE NULL,
    estado_operacional_pd        VARCHAR2(20) NULL
);
ALTER TABLE Ponto_Distribuicao ADD CONSTRAINT pk_ponto_distribuicao PRIMARY KEY (codigo_pd);
ALTER TABLE Ponto_Distribuicao
    ADD CONSTRAINT ck_ponto_estado_operacional
    CHECK (estado_operacional_pd IN ('Activo', 'Inactivo', 'Em Manutencao'));

CREATE TABLE Retirada_Agua_Pendente (
    cod_retirada_pendente     NUMBER(7) NOT NULL,
    codigo_fb                 NUMBER(7) NOT NULL,
    codigo_cota               NUMBER(7) NOT NULL,
    codigo_pd                 NUMBER(7) NOT NULL,
    volume_retirado           NUMBER(8,2) NOT NULL,
    pessoa_coleta             VARCHAR2(60) NOT NULL,
    observacao                VARCHAR2(200),
    data_pedido               TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    saldo_cota_snapshot       NUMBER(8,2) NOT NULL,
    periodo_validade_snapshot DATE,
    estado_familia_snapshot   VARCHAR2(20) NOT NULL,
    estado_pendente           VARCHAR2(20) DEFAULT 'PENDENTE' NOT NULL,
    data_processamento        TIMESTAMP,
    mensagem_processamento    VARCHAR2(4000)
);
ALTER TABLE Retirada_Agua_Pendente
    ADD CONSTRAINT pk_retirada_agua_pendente PRIMARY KEY (cod_retirada_pendente);
ALTER TABLE Retirada_Agua_Pendente
    ADD CONSTRAINT ck_ret_agua_pend_estado
    CHECK (estado_pendente IN ('PENDENTE', 'APROVADA', 'REJEITADA'));
ALTER TABLE Retirada_Agua_Pendente
    ADD CONSTRAINT fk_ret_agua_pend_ponto FOREIGN KEY (codigo_pd)
    REFERENCES Ponto_Distribuicao (codigo_pd);

CREATE TABLE Registro_Consumo (
    codigo_rc              NUMBER(7) NOT NULL,
    codigo_fb              NUMBER(7) NOT NULL,
    codigo_pd              NUMBER(7) NOT NULL,
    codigo_cota            NUMBER(7) NOT NULL,
    data_hora_rc           TIMESTAMP NULL,
    volume_retirado_rc     NUMBER(8,2) NULL,
    pessoa_coleta_rc       VARCHAR2(60) NULL,
    metodo_autenticacao_rc VARCHAR2(60) NULL,
    saldo_cota_rc          NUMBER(8,2) NULL,
    observacao_rc          VARCHAR2(200) NULL
);
ALTER TABLE Registro_Consumo ADD CONSTRAINT pk_registro_consumo PRIMARY KEY (codigo_rc);
ALTER TABLE Registro_Consumo
    ADD CONSTRAINT ck_reg_consumo_metodo_auth CHECK (metodo_autenticacao_rc = 'MANUAL');
ALTER TABLE Registro_Consumo
    ADD CONSTRAINT fk_reg_consumo_ponto_distr FOREIGN KEY (codigo_pd)
    REFERENCES Ponto_Distribuicao (codigo_pd);

CREATE TABLE Historico_Manutencao (
    cod_historico_manutencao NUMBER(7) NOT NULL,
    codigo_pd                NUMBER(7) NOT NULL,
    equipe_id                NUMBER(7) NOT NULL,
    data_manutencao          DATE NULL,
    tipo_manutencao          VARCHAR2(300) NULL
);
ALTER TABLE Historico_Manutencao ADD CONSTRAINT pk_historico_manutencao PRIMARY KEY (cod_historico_manutencao);
ALTER TABLE Historico_Manutencao
    ADD CONSTRAINT fk_hist_manut_ponto_distr FOREIGN KEY (codigo_pd)
    REFERENCES Ponto_Distribuicao (codigo_pd);

CREATE TABLE Historico_Abastecimento (
    cod_abastecimento    NUMBER(7) NOT NULL,
    codigo_pd            NUMBER(7) NOT NULL,
    codigo_rh            NUMBER(7) NOT NULL,
    volume_abastecido    NUMBER(12,2) NOT NULL,
    data_inicio          DATE NOT NULL,
    data_fim             DATE NULL,
    duracao_horas        NUMBER(12,6) NOT NULL,
    estado_abastecimento VARCHAR2(30) NOT NULL
);
ALTER TABLE Historico_Abastecimento ADD CONSTRAINT pk_historico_abastecimento PRIMARY KEY (cod_abastecimento);
ALTER TABLE Historico_Abastecimento
    ADD CONSTRAINT fk_hist_abast_ponto_distr FOREIGN KEY (codigo_pd)
    REFERENCES Ponto_Distribuicao (codigo_pd);
ALTER TABLE Historico_Abastecimento
    ADD CONSTRAINT ck_hist_abast_estado CHECK (
        estado_abastecimento IN ('Em curso', 'Concluido', 'Cancelado')
    );
