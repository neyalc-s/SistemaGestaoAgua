SET ECHO OFF;

SET SERVEROUTPUT OFF;

DECLARE
    c_pd1 CONSTANT NUMBER := 4000;
    c_pd2 CONSTANT NUMBER := 4001;
    c_pd3 CONSTANT NUMBER := 4002;
    c_pd4 CONSTANT NUMBER := 4003;
    c_pd5 CONSTANT NUMBER := 4004;
    v_loc1 NUMBER;
    v_loc2 NUMBER;
    v_loc3 NUMBER;
    v_loc4 NUMBER;
    v_loc5 NUMBER;
    v_nec1 NUMBER;
    v_nec2 NUMBER;
    v_nec3 NUMBER;
    v_nec4 NUMBER;
    v_nec5 NUMBER;
    v_nec6 NUMBER;
    v_nec7 NUMBER;
    v_nec8 NUMBER;
    v_nec9 NUMBER;
    v_nec10 NUMBER;
    v_nec11 NUMBER;
    v_nec12 NUMBER;
    v_nec13 NUMBER;
    v_nec14 NUMBER;
    v_nec15 NUMBER;
    v_fb1 NUMBER;
    v_fb2 NUMBER;
    v_fb3 NUMBER;
    v_fb4 NUMBER;
    v_fb5 NUMBER;
    v_fb6 NUMBER;
    v_fb7 NUMBER;
    v_fb8 NUMBER;
    v_fb9 NUMBER;
    v_fb10 NUMBER;
    v_ca1 NUMBER;
    v_ca2 NUMBER;
    v_ca3 NUMBER;
    v_ca4 NUMBER;
    v_ca5 NUMBER;
    v_ca6 NUMBER;
    v_ca7 NUMBER;
    v_ca8 NUMBER;
    v_ca9 NUMBER;
    v_ca10 NUMBER;
    v_ca11 NUMBER;
    v_ca12 NUMBER;
    v_ca13 NUMBER;
    v_ca14 NUMBER;
    v_ca15 NUMBER;
    v_ca16 NUMBER;
    v_ca17 NUMBER;
    v_ca18 NUMBER;
    v_ca19 NUMBER;
    v_ca20 NUMBER;
    v_ca21 NUMBER;
    v_ca22 NUMBER;
    v_ca23 NUMBER;
    v_ca24 NUMBER;
    v_ca25 NUMBER;
    v_ca26 NUMBER;
    v_ca27 NUMBER;
    v_ca28 NUMBER;
    v_ca29 NUMBER;
    v_ca30 NUMBER;
BEGIN
    INSERT INTO Localizacao (
        cod_localizacao, aldeia, coordenadas_gps
    ) VALUES (
        seq_localizacao.NEXTVAL, 'Aldeia Nhamatanda', 'GPS:-19.123,34.567'
    ) RETURNING cod_localizacao INTO v_loc1;

    INSERT INTO Localizacao (
        cod_localizacao, aldeia, coordenadas_gps
    ) VALUES (
        seq_localizacao.NEXTVAL, 'Aldeia Gorongoza', 'GPS:-18.675,34.095'
    ) RETURNING cod_localizacao INTO v_loc2;

    INSERT INTO Localizacao (
        cod_localizacao, aldeia, coordenadas_gps
    ) VALUES (
        seq_localizacao.NEXTVAL, 'Aldeia Muanza', 'GPS:-19.556,34.221'
    ) RETURNING cod_localizacao INTO v_loc3;

    INSERT INTO Localizacao (
        cod_localizacao, aldeia, coordenadas_gps
    ) VALUES (
        seq_localizacao.NEXTVAL, 'Aldeia Chitengo', 'GPS:-18.954,34.251'
    ) RETURNING cod_localizacao INTO v_loc4;

    INSERT INTO Localizacao (
        cod_localizacao, aldeia, coordenadas_gps
    ) VALUES (
        seq_localizacao.NEXTVAL, 'Aldeia Metuchira', 'GPS:-18.894,34.467'
    ) RETURNING cod_localizacao INTO v_loc5;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Necessidade de aumento de cota'
    ) RETURNING cod_necessidade INTO v_nec1;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Instalacao de reservatorio'
    ) RETURNING cod_necessidade INTO v_nec2;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Treinamento de uso racional'
    ) RETURNING cod_necessidade INTO v_nec3;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Reparacao da bomba'
    ) RETURNING cod_necessidade INTO v_nec4;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Monitoramento de qualidade'
    ) RETURNING cod_necessidade INTO v_nec5;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Instalacao de filtro de agua'
    ) RETURNING cod_necessidade INTO v_nec6;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Treinamento sobre cloracao'
    ) RETURNING cod_necessidade INTO v_nec7;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Construcao de cisterna'
    ) RETURNING cod_necessidade INTO v_nec8;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Substituicao da bomba manual'
    ) RETURNING cod_necessidade INTO v_nec9;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Instalacao de painel solar para bomba'
    ) RETURNING cod_necessidade INTO v_nec10;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Analise mensal da qualidade da agua'
    ) RETURNING cod_necessidade INTO v_nec11;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Substituicao de torneiras no chafariz'
    ) RETURNING cod_necessidade INTO v_nec12;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Extensao da rede de distribuicao'
    ) RETURNING cod_necessidade INTO v_nec13;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Perfuracao de poco mais profundo'
    ) RETURNING cod_necessidade INTO v_nec14;

    INSERT INTO Necessidade (
        cod_necessidade, descricao_necessidade
    ) VALUES (
        seq_necessidade.NEXTVAL, 'Construcao de tanque elevado'
    ) RETURNING cod_necessidade INTO v_nec15;

    INSERT INTO Familia_Beneficiaria (
        codigo_fb, codigo_pd, cod_localizacao, nome_responsavel_fb, num_membros_fb, perfil_socioeconomico_fb, contacto_fb, estado_fb
    ) VALUES (
        seq_familia_beneficiaria.NEXTVAL, c_pd1, v_loc1, 'Ana Joaquim', 5, 'Baixa renda', '842111111', 'Activo'
    ) RETURNING codigo_fb INTO v_fb1;

    INSERT INTO Familia_Beneficiaria (
        codigo_fb, codigo_pd, cod_localizacao, nome_responsavel_fb, num_membros_fb, perfil_socioeconomico_fb, contacto_fb, estado_fb
    ) VALUES (
        seq_familia_beneficiaria.NEXTVAL, c_pd2, v_loc2, 'Pedro Matola', 6, 'Media renda', '842222222', 'Activo'
    ) RETURNING codigo_fb INTO v_fb2;

    INSERT INTO Familia_Beneficiaria (
        codigo_fb, codigo_pd, cod_localizacao, nome_responsavel_fb, num_membros_fb, perfil_socioeconomico_fb, contacto_fb, estado_fb
    ) VALUES (
        seq_familia_beneficiaria.NEXTVAL, c_pd3, v_loc3, 'Luisa Andre', 4, 'Baixa renda', '842333333', 'Activo'
    ) RETURNING codigo_fb INTO v_fb3;

    INSERT INTO Familia_Beneficiaria (
        codigo_fb, codigo_pd, cod_localizacao, nome_responsavel_fb, num_membros_fb, perfil_socioeconomico_fb, contacto_fb, estado_fb
    ) VALUES (
        seq_familia_beneficiaria.NEXTVAL, c_pd4, v_loc4, 'Rafael Muchele', 7, 'Baixa renda', '842444444', 'Activo'
    ) RETURNING codigo_fb INTO v_fb4;

    INSERT INTO Familia_Beneficiaria (
        codigo_fb, codigo_pd, cod_localizacao, nome_responsavel_fb, num_membros_fb, perfil_socioeconomico_fb, contacto_fb, estado_fb
    ) VALUES (
        seq_familia_beneficiaria.NEXTVAL, c_pd5, v_loc5, 'Marta Nunes', 3, 'Media renda', '842555555', 'Activo'
    ) RETURNING codigo_fb INTO v_fb5;

    INSERT INTO Familia_Beneficiaria (
        codigo_fb, codigo_pd, cod_localizacao, nome_responsavel_fb, num_membros_fb, perfil_socioeconomico_fb, contacto_fb, estado_fb
    ) VALUES (
        seq_familia_beneficiaria.NEXTVAL, c_pd1, v_loc1, 'Celina Chissano', 8, 'Baixa renda', '843111111', 'Activo'
    ) RETURNING codigo_fb INTO v_fb6;

    INSERT INTO Familia_Beneficiaria (
        codigo_fb, codigo_pd, cod_localizacao, nome_responsavel_fb, num_membros_fb, perfil_socioeconomico_fb, contacto_fb, estado_fb
    ) VALUES (
        seq_familia_beneficiaria.NEXTVAL, c_pd2, v_loc2, 'Manuel Sithole', 2, 'Media renda', '843222222', 'Activo'
    ) RETURNING codigo_fb INTO v_fb7;

    INSERT INTO Familia_Beneficiaria (
        codigo_fb, codigo_pd, cod_localizacao, nome_responsavel_fb, num_membros_fb, perfil_socioeconomico_fb, contacto_fb, estado_fb
    ) VALUES (
        seq_familia_beneficiaria.NEXTVAL, c_pd3, v_loc3, 'Esperanca Dique', 9, 'Baixa renda', '843333333', 'Activo'
    ) RETURNING codigo_fb INTO v_fb8;

    INSERT INTO Familia_Beneficiaria (
        codigo_fb, codigo_pd, cod_localizacao, nome_responsavel_fb, num_membros_fb, perfil_socioeconomico_fb, contacto_fb, estado_fb
    ) VALUES (
        seq_familia_beneficiaria.NEXTVAL, c_pd4, v_loc4, 'Antonio Bambo', 4, 'Baixa renda', '843444444', 'Activo'
    ) RETURNING codigo_fb INTO v_fb9;

    INSERT INTO Familia_Beneficiaria (
        codigo_fb, codigo_pd, cod_localizacao, nome_responsavel_fb, num_membros_fb, perfil_socioeconomico_fb, contacto_fb, estado_fb
    ) VALUES (
        seq_familia_beneficiaria.NEXTVAL, c_pd5, v_loc5, 'Sara Macuacua', 6, 'Media renda', '843555555', 'Activo'
    ) RETURNING codigo_fb INTO v_fb10;

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec1, v_fb1
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec2, v_fb2
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec3, v_fb3
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec4, v_fb4
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec5, v_fb5
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec6, v_fb1
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec7, v_fb1
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec8, v_fb2
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec9, v_fb2
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec10, v_fb3
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec11, v_fb3
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec12, v_fb4
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec13, v_fb4
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec14, v_fb5
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec15, v_fb5
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec1, v_fb6
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec6, v_fb6
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec3, v_fb7
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec8, v_fb7
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec4, v_fb8
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec11, v_fb8
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec5, v_fb9
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec12, v_fb9
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec2, v_fb10
    );

    INSERT INTO NECESS_ASSOC_FAMILIA (
        cod_necessidade, codigo_fb
    ) VALUES (
        v_nec14, v_fb10
    );

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb1, 250.00, TO_DATE('2026-05-24 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Verao', 'SIM', 225.00
    ) RETURNING codigo_cota INTO v_ca1;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb1, 250.00, TO_DATE('2026-05-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Transicao Seca', 'SIM', 224.50
    ) RETURNING codigo_cota INTO v_ca6;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb1, 250.00, TO_DATE('2026-06-07 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Fim de Ano', 'NAO', 250.00
    ) RETURNING codigo_cota INTO v_ca7;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb2, 300.00, TO_DATE('2026-05-24 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Inverno', 'NAO', 265.00
    ) RETURNING codigo_cota INTO v_ca2;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb2, 300.00, TO_DATE('2026-05-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Pico Seco', 'SIM', 269.25
    ) RETURNING codigo_cota INTO v_ca8;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb2, 300.00, TO_DATE('2026-06-07 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Inicio Chuvoso', 'SIM', 300.00
    ) RETURNING codigo_cota INTO v_ca9;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb3, 200.00, TO_DATE('2026-05-24 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Chuvoso', 'SIM', 184.60
    ) RETURNING codigo_cota INTO v_ca3;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb3, 200.00, TO_DATE('2026-05-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Alta Demanda', 'SIM', 180.00
    ) RETURNING codigo_cota INTO v_ca10;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb3, 200.00, TO_DATE('2026-06-07 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Fim de Ano', 'NAO', 200.00
    ) RETURNING codigo_cota INTO v_ca11;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb4, 350.00, TO_DATE('2026-05-24 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Seco', 'SIM', 319.90
    ) RETURNING codigo_cota INTO v_ca4;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb4, 350.00, TO_DATE('2026-05-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Pico Seco', 'SIM', 321.50
    ) RETURNING codigo_cota INTO v_ca12;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb4, 350.00, TO_DATE('2026-06-07 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Normal', 'SIM', 350.00
    ) RETURNING codigo_cota INTO v_ca13;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb5, 150.00, TO_DATE('2026-05-24 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Normal', 'NAO', 111.10
    ) RETURNING codigo_cota INTO v_ca5;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb5, 150.00, TO_DATE('2026-05-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Transicao Seca', 'NAO', 118.00
    ) RETURNING codigo_cota INTO v_ca14;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb5, 150.00, TO_DATE('2026-06-07 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Inicio Chuvoso', 'SIM', 150.00
    ) RETURNING codigo_cota INTO v_ca15;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb6, 400.00, TO_DATE('2026-05-24 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Seco', 'SIM', 330.00
    ) RETURNING codigo_cota INTO v_ca16;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb6, 400.00, TO_DATE('2026-05-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Pico Seco', 'SIM', 345.00
    ) RETURNING codigo_cota INTO v_ca17;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb6, 400.00, TO_DATE('2026-06-07 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Normal', 'NAO', 400.00
    ) RETURNING codigo_cota INTO v_ca18;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb7, 100.00, TO_DATE('2026-05-24 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Chuvoso', 'NAO', 78.00
    ) RETURNING codigo_cota INTO v_ca19;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb7, 100.00, TO_DATE('2026-05-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Transicao Seca', 'SIM', 89.50
    ) RETURNING codigo_cota INTO v_ca20;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb7, 100.00, TO_DATE('2026-06-07 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Fim de Ano', 'SIM', 100.00
    ) RETURNING codigo_cota INTO v_ca21;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb8, 450.00, TO_DATE('2026-05-24 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Alta Demanda', 'SIM', 390.00
    ) RETURNING codigo_cota INTO v_ca22;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb8, 450.00, TO_DATE('2026-05-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Pico Seco', 'SIM', 410.00
    ) RETURNING codigo_cota INTO v_ca23;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb8, 450.00, TO_DATE('2026-06-07 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Inicio Chuvoso', 'NAO', 450.00
    ) RETURNING codigo_cota INTO v_ca24;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb9, 200.00, TO_DATE('2026-05-24 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Normal', 'NAO', 168.00
    ) RETURNING codigo_cota INTO v_ca25;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb9, 200.00, TO_DATE('2026-05-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Transicao Seca', 'SIM', 179.00
    ) RETURNING codigo_cota INTO v_ca26;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb9, 200.00, TO_DATE('2026-06-07 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Fim de Ano', 'SIM', 200.00
    ) RETURNING codigo_cota INTO v_ca27;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb10, 300.00, TO_DATE('2026-05-24 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Verao', 'SIM', 240.00
    ) RETURNING codigo_cota INTO v_ca28;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb10, 300.00, TO_DATE('2026-05-31 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Transicao Seca', 'SIM', 267.50
    ) RETURNING codigo_cota INTO v_ca29;

    INSERT INTO Cota_Agua (
        codigo_cota, codigo_fb, volume_semanal_ca, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, saldo_disponivel_ca
    ) VALUES (
        seq_cota_agua.NEXTVAL, v_fb10, 300.00, TO_DATE('2026-06-07 23:59:59', 'YYYY-MM-DD HH24:MI:SS'), 'Inicio Chuvoso', 'NAO', 300.00
    ) RETURNING codigo_cota INTO v_ca30;

    COMMIT;
END;
/
