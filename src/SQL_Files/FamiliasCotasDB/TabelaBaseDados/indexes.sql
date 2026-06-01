SET ECHO OFF;

CREATE INDEX ix_fb_pd_estado ON Familia_Beneficiaria (codigo_pd, estado_fb);
CREATE INDEX ix_fb_localizacao ON Familia_Beneficiaria (cod_localizacao);
CREATE INDEX ix_fb_nome_upper ON Familia_Beneficiaria (UPPER(nome_responsavel_fb));
CREATE INDEX ix_fb_contacto_trim ON Familia_Beneficiaria (TRIM(contacto_fb));
CREATE INDEX ix_loc_aldeia_upper ON Localizacao (UPPER(aldeia));
CREATE INDEX ix_cota_fb_cod ON Cota_Agua (codigo_fb, codigo_cota DESC);
CREATE INDEX ix_cota_fb_validade ON Cota_Agua (codigo_fb, periodo_validade_ca);
CREATE INDEX ix_cota_fb_transf ON Cota_Agua (codigo_fb, transferencia_autorizada_ca);
DECLARE
    v_total NUMBER;
BEGIN
    SELECT COUNT(*)
      INTO v_total
      FROM user_indexes ui
     WHERE ui.table_name = 'COTA_AGUA'
       AND ui.uniqueness = 'UNIQUE'
       AND (
           ui.index_name = 'UK_COTA_FAMILIA_SEMANA'
           OR ui.index_name = 'SYS_COTA_FAMILIA_SEMANA'
       );

    IF v_total = 0 THEN
        EXECUTE IMMEDIATE
            'CREATE UNIQUE INDEX uk_cota_familia_semana ' ||
            'ON Cota_Agua (codigo_fb, TRUNC(periodo_validade_ca, ''IW''))';
    END IF;
END;
/
CREATE INDEX ix_naf_fb_nec ON NECESS_ASSOC_FAMILIA (codigo_fb, cod_necessidade);
CREATE INDEX ix_nec_desc_upper ON Necessidade (UPPER(descricao_necessidade));
