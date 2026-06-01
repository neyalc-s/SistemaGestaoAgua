SET ECHO OFF;

CREATE INDEX ix_rh_tipo ON Recurso_Hidrico (tipo_rh);
CREATE INDEX ix_rh_loc_upper ON Recurso_Hidrico (UPPER(localizacao_rh));
CREATE INDEX ix_rh_sazonalidade ON Recurso_Hidrico (sazonalidade_rh);
CREATE UNIQUE INDEX ux_param_nome_upper ON Parametro_Qualidade (UPPER(TRIM(nome_parametro)));
CREATE INDEX ix_mp_responsavel ON Medida_Proteccao (cod_responsavel);
CREATE INDEX ix_mp_desc_upper ON Medida_Proteccao (UPPER(descricao_medida));
CREATE INDEX ix_rap_rh_medida ON REC_APLICA_MED_PROT (codigo_rh, cod_medida_proteccao);
CREATE INDEX ix_mqa_rh_data ON Medicao_Qualidade_Agua (codigo_rh, data_medicao DESC);
CREATE INDEX ix_mqa_param_data ON Medicao_Qualidade_Agua (codigo_parametro, data_medicao DESC);
CREATE INDEX ix_mqa_eq_data ON Medicao_Qualidade_Agua (equipe_id, data_medicao DESC);
CREATE INDEX ix_tc_doad_data ON Transferencia_Cota (cod_fam_doadora_tc, data_aprovacao_tc DESC);
CREATE INDEX ix_tc_rec_data ON Transferencia_Cota (cod_fam_receptora_tc, data_aprovacao_tc DESC);
CREATE INDEX ix_tc_data ON Transferencia_Cota (data_aprovacao_tc DESC);
CREATE INDEX ix_fdr_fb_tc ON FAMILIA_DOA_REC_TRANSFER (codigo_fb, codigo_tc);
CREATE INDEX ix_fdr_tipo_tc ON FAMILIA_DOA_REC_TRANSFER (tipo_participacao, codigo_tc);
CREATE INDEX ix_tac_fb_cota ON TRANSFER_ASSOC_COTA (codigo_fb, codigo_cota, codigo_tc);
