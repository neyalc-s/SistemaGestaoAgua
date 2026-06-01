SET ECHO OFF;

CREATE UNIQUE INDEX ux_comite_nome_upper ON Comite (UPPER(TRIM(nome_comite)));
CREATE UNIQUE INDEX ux_eq_nome_upper ON Equipe_Tecnica (UPPER(TRIM(nome)));
CREATE INDEX ix_eq_area_upper ON Equipe_Tecnica (UPPER(area_actuacao));
CREATE INDEX ix_eq_contacto ON Equipe_Tecnica (contacto);
CREATE INDEX ix_aq_param_eq ON Analista_Qualidade (codigo_parametro, equipe_id);
CREATE INDEX ix_fm_eq_nome ON Ferramenta_Manutencao (equipe_id, nome_ferramenta);
CREATE INDEX ix_eqa_eq ON Equipamento_Analista (equipe_id);
CREATE INDEX ix_me_eq ON Material_Educador (equipe_id);
