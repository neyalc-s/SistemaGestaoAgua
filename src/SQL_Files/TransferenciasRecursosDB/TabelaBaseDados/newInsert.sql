SET ECHO OFF;

SET SERVEROUTPUT OFF;

DECLARE
    c_fb1 CONSTANT NUMBER := 5000;
    c_fb2 CONSTANT NUMBER := 5001;
    c_fb3 CONSTANT NUMBER := 5002;
    c_fb4 CONSTANT NUMBER := 5003;
    c_fb5 CONSTANT NUMBER := 5004;
    c_fb6 CONSTANT NUMBER := 5005;
    c_fb7 CONSTANT NUMBER := 5006;
    c_fb8 CONSTANT NUMBER := 5007;
    c_fb9 CONSTANT NUMBER := 5008;
    c_fb10 CONSTANT NUMBER := 5009;
    c_ca1 CONSTANT NUMBER := 6000;
    c_ca2 CONSTANT NUMBER := 6003;
    c_ca3 CONSTANT NUMBER := 6006;
    c_ca4 CONSTANT NUMBER := 6009;
    c_ca5 CONSTANT NUMBER := 6012;
    c_ca6 CONSTANT NUMBER := 6001;
    c_ca7 CONSTANT NUMBER := 6002;
    c_ca8 CONSTANT NUMBER := 6004;
    c_ca9 CONSTANT NUMBER := 6005;
    c_ca10 CONSTANT NUMBER := 6007;
    c_ca11 CONSTANT NUMBER := 6008;
    c_ca12 CONSTANT NUMBER := 6010;
    c_ca13 CONSTANT NUMBER := 6011;
    c_ca14 CONSTANT NUMBER := 6013;
    c_ca15 CONSTANT NUMBER := 6014;
    c_ca16 CONSTANT NUMBER := 6015;
    c_ca17 CONSTANT NUMBER := 6016;
    c_ca18 CONSTANT NUMBER := 6017;
    c_ca19 CONSTANT NUMBER := 6018;
    c_ca20 CONSTANT NUMBER := 6019;
    c_ca21 CONSTANT NUMBER := 6020;
    c_ca22 CONSTANT NUMBER := 6021;
    c_ca23 CONSTANT NUMBER := 6022;
    c_ca24 CONSTANT NUMBER := 6023;
    c_ca25 CONSTANT NUMBER := 6024;
    c_ca26 CONSTANT NUMBER := 6025;
    c_ca27 CONSTANT NUMBER := 6026;
    c_ca28 CONSTANT NUMBER := 6027;
    c_ca29 CONSTANT NUMBER := 6028;
    c_ca30 CONSTANT NUMBER := 6029;
    c_eqp1 CONSTANT NUMBER := 1000;
    c_eqp2 CONSTANT NUMBER := 1001;
    c_eqp3 CONSTANT NUMBER := 1002;
    c_eqp4 CONSTANT NUMBER := 1003;
    c_eqp5 CONSTANT NUMBER := 1004;
    c_eqp6 CONSTANT NUMBER := 1005;
    c_eqp7 CONSTANT NUMBER := 1006;
    c_eqp8 CONSTANT NUMBER := 1007;
    c_eqp9 CONSTANT NUMBER := 1008;
    c_eqp10 CONSTANT NUMBER := 1009;
    v_par1 NUMBER;
    v_par2 NUMBER;
    v_par3 NUMBER;
    v_par4 NUMBER;
    v_par5 NUMBER;
    v_res1 NUMBER;
    v_res2 NUMBER;
    v_res3 NUMBER;
    v_res4 NUMBER;
    v_res5 NUMBER;
    v_res6 NUMBER;
    v_res7 NUMBER;
    v_res8 NUMBER;
    v_rh1 NUMBER;
    v_rh2 NUMBER;
    v_rh3 NUMBER;
    v_rh4 NUMBER;
    v_rh5 NUMBER;
    v_mp1 NUMBER;
    v_mp2 NUMBER;
    v_mp3 NUMBER;
    v_mp4 NUMBER;
    v_mp5 NUMBER;
    v_mp6 NUMBER;
    v_mp7 NUMBER;
    v_mp8 NUMBER;
    v_mp9 NUMBER;
    v_mp10 NUMBER;
    v_mp11 NUMBER;
    v_mp12 NUMBER;
    v_mp13 NUMBER;
    v_mp14 NUMBER;
    v_mp15 NUMBER;
    v_mq1 NUMBER;
    v_mq2 NUMBER;
    v_mq3 NUMBER;
    v_mq4 NUMBER;
    v_mq5 NUMBER;
    v_mq6 NUMBER;
    v_mq7 NUMBER;
    v_mq8 NUMBER;
    v_mq9 NUMBER;
    v_mq10 NUMBER;
    v_mq11 NUMBER;
    v_mq12 NUMBER;
    v_mq13 NUMBER;
    v_mq14 NUMBER;
    v_mq15 NUMBER;
    v_mq16 NUMBER;
    v_mq17 NUMBER;
    v_mq18 NUMBER;
    v_mq19 NUMBER;
    v_mq20 NUMBER;
    v_mq21 NUMBER;
    v_mq22 NUMBER;
    v_mq23 NUMBER;
    v_mq24 NUMBER;
    v_mq25 NUMBER;
    v_tc1 NUMBER;
    v_tc2 NUMBER;
    v_tc3 NUMBER;
    v_tc4 NUMBER;
    v_tc5 NUMBER;
BEGIN
    INSERT INTO Parametro_Qualidade (
        codigo_parametro, nome_parametro, unidade_padrao
    ) VALUES (
        seq_parametro_qualidade.NEXTVAL, 'pH', 'Unid'
    ) RETURNING codigo_parametro INTO v_par1;

    INSERT INTO Parametro_Qualidade (
        codigo_parametro, nome_parametro, unidade_padrao
    ) VALUES (
        seq_parametro_qualidade.NEXTVAL, 'Turbidez', 'NTU'
    ) RETURNING codigo_parametro INTO v_par2;

    INSERT INTO Parametro_Qualidade (
        codigo_parametro, nome_parametro, unidade_padrao
    ) VALUES (
        seq_parametro_qualidade.NEXTVAL, 'Temperatura', 'C'
    ) RETURNING codigo_parametro INTO v_par3;

    INSERT INTO Parametro_Qualidade (
        codigo_parametro, nome_parametro, unidade_padrao
    ) VALUES (
        seq_parametro_qualidade.NEXTVAL, 'Cloro Residual', 'mg/L'
    ) RETURNING codigo_parametro INTO v_par4;

    INSERT INTO Parametro_Qualidade (
        codigo_parametro, nome_parametro, unidade_padrao
    ) VALUES (
        seq_parametro_qualidade.NEXTVAL, 'Oxigenio Dissolvido', 'mg/L'
    ) RETURNING codigo_parametro INTO v_par5;

    INSERT INTO Medida_Prot_Responsavel (
        cod_responsavel, nome_responsavel
    ) VALUES (
        seq_medida_prot_responsavel.NEXTVAL, 'Carlos Jaime'
    ) RETURNING cod_responsavel INTO v_res1;

    INSERT INTO Medida_Prot_Responsavel (
        cod_responsavel, nome_responsavel
    ) VALUES (
        seq_medida_prot_responsavel.NEXTVAL, 'Joao Luis'
    ) RETURNING cod_responsavel INTO v_res2;

    INSERT INTO Medida_Prot_Responsavel (
        cod_responsavel, nome_responsavel
    ) VALUES (
        seq_medida_prot_responsavel.NEXTVAL, 'Fatima Jose'
    ) RETURNING cod_responsavel INTO v_res3;

    INSERT INTO Medida_Prot_Responsavel (
        cod_responsavel, nome_responsavel
    ) VALUES (
        seq_medida_prot_responsavel.NEXTVAL, 'Mario Faite'
    ) RETURNING cod_responsavel INTO v_res4;

    INSERT INTO Medida_Prot_Responsavel (
        cod_responsavel, nome_responsavel
    ) VALUES (
        seq_medida_prot_responsavel.NEXTVAL, 'Helena Dinis'
    ) RETURNING cod_responsavel INTO v_res5;

    INSERT INTO Medida_Prot_Responsavel (
        cod_responsavel, nome_responsavel
    ) VALUES (
        seq_medida_prot_responsavel.NEXTVAL, 'Anabela Cossa'
    ) RETURNING cod_responsavel INTO v_res6;

    INSERT INTO Medida_Prot_Responsavel (
        cod_responsavel, nome_responsavel
    ) VALUES (
        seq_medida_prot_responsavel.NEXTVAL, 'Eusebio Matola'
    ) RETURNING cod_responsavel INTO v_res7;

    INSERT INTO Medida_Prot_Responsavel (
        cod_responsavel, nome_responsavel
    ) VALUES (
        seq_medida_prot_responsavel.NEXTVAL, 'Lurdes Manhica'
    ) RETURNING cod_responsavel INTO v_res8;

    INSERT INTO Recurso_Hidrico (
        codigo_rh, tipo_rh, localizacao_rh, volume_rh, sazonalidade_rh, vulnerabilidade_rh, nivel_exploracao_rh
    ) VALUES (
        seq_recurso_hidrico.NEXTVAL, 'Subterranea', 'Nhamatanda Centro', 300.00, 'Permanente', 'Risco de contaminacao por latrinas proximas', 'Medio'
    ) RETURNING codigo_rh INTO v_rh1;

    INSERT INTO Recurso_Hidrico (
        codigo_rh, tipo_rh, localizacao_rh, volume_rh, sazonalidade_rh, vulnerabilidade_rh, nivel_exploracao_rh
    ) VALUES (
        seq_recurso_hidrico.NEXTVAL, 'Superficial', 'Rio Gorongoza', 350.00, 'Chuvosa', 'Risco de cheias e turvacao sazonal', 'Baixo'
    ) RETURNING codigo_rh INTO v_rh2;

    INSERT INTO Recurso_Hidrico (
        codigo_rh, tipo_rh, localizacao_rh, volume_rh, sazonalidade_rh, vulnerabilidade_rh, nivel_exploracao_rh
    ) VALUES (
        seq_recurso_hidrico.NEXTVAL, 'Superficial', 'Metuchira', 250.00, 'Seca', 'Reducao acentuada do nivel da agua na epoca seca', 'Medio'
    ) RETURNING codigo_rh INTO v_rh3;

    INSERT INTO Recurso_Hidrico (
        codigo_rh, tipo_rh, localizacao_rh, volume_rh, sazonalidade_rh, vulnerabilidade_rh, nivel_exploracao_rh
    ) VALUES (
        seq_recurso_hidrico.NEXTVAL, 'Subterranea', 'Muanza Sul', 230.00, 'Permanente', 'Risco de sobre-exploracao por uso comunitario intenso', 'Alto'
    ) RETURNING codigo_rh INTO v_rh4;

    INSERT INTO Recurso_Hidrico (
        codigo_rh, tipo_rh, localizacao_rh, volume_rh, sazonalidade_rh, vulnerabilidade_rh, nivel_exploracao_rh
    ) VALUES (
        seq_recurso_hidrico.NEXTVAL, 'Pluvial', 'Chitengo Norte', 200.00, 'Chuvosa', 'Dependencia elevada da chuva e armazenamento limitado', 'Medio'
    ) RETURNING codigo_rh INTO v_rh5;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res1, 'Cercamento da nascente'
    ) RETURNING cod_medida_proteccao INTO v_mp1;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res2, 'Plantio de arvores'
    ) RETURNING cod_medida_proteccao INTO v_mp2;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res3, 'Criacao de zona de protecao'
    ) RETURNING cod_medida_proteccao INTO v_mp3;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res4, 'Manutencao da cobertura vegetal'
    ) RETURNING cod_medida_proteccao INTO v_mp4;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res5, 'Monitoramento continuo'
    ) RETURNING cod_medida_proteccao INTO v_mp5;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res6, 'Limpeza periodica da area de captacao'
    ) RETURNING cod_medida_proteccao INTO v_mp6;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res7, 'Controlo de erosao nas margens'
    ) RETURNING cod_medida_proteccao INTO v_mp7;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res8, 'Sinalizacao da zona protegida'
    ) RETURNING cod_medida_proteccao INTO v_mp8;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res1, 'Vedacao contra entrada de animais'
    ) RETURNING cod_medida_proteccao INTO v_mp9;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res2, 'Fiscalizacao comunitaria semanal'
    ) RETURNING cod_medida_proteccao INTO v_mp10;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res3, 'Controlo de residuos solidos'
    ) RETURNING cod_medida_proteccao INTO v_mp11;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res4, 'Reforco da drenagem periferica'
    ) RETURNING cod_medida_proteccao INTO v_mp12;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res5, 'Inspeccao sanitaria mensal'
    ) RETURNING cod_medida_proteccao INTO v_mp13;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res6, 'Proteccao contra infiltracao superficial'
    ) RETURNING cod_medida_proteccao INTO v_mp14;

    INSERT INTO Medida_Proteccao (
        cod_medida_proteccao, cod_responsavel, descricao_medida
    ) VALUES (
        seq_medida_proteccao.NEXTVAL, v_res7, 'Sensibilizacao sobre uso seguro da fonte'
    ) RETURNING cod_medida_proteccao INTO v_mp15;

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp1, v_rh1, TO_DATE('2026-03-01', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp6, v_rh1, TO_DATE('2026-03-04', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp8, v_rh1, TO_DATE('2026-03-07', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp9, v_rh1, TO_DATE('2026-03-11', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp13, v_rh1, TO_DATE('2026-03-15', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp14, v_rh1, TO_DATE('2026-03-18', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp2, v_rh2, TO_DATE('2026-03-05', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp5, v_rh2, TO_DATE('2026-03-08', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp7, v_rh2, TO_DATE('2026-03-13', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp10, v_rh2, TO_DATE('2026-03-16', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp11, v_rh2, TO_DATE('2026-03-19', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp15, v_rh2, TO_DATE('2026-03-22', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp3, v_rh3, TO_DATE('2026-03-10', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp5, v_rh3, TO_DATE('2026-03-14', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp6, v_rh3, TO_DATE('2026-03-17', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp7, v_rh3, TO_DATE('2026-03-21', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp12, v_rh3, TO_DATE('2026-03-24', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp13, v_rh3, TO_DATE('2026-03-27', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp4, v_rh4, TO_DATE('2026-03-12', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp5, v_rh4, TO_DATE('2026-03-18', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp8, v_rh4, TO_DATE('2026-03-23', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp9, v_rh4, TO_DATE('2026-03-28', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp10, v_rh4, TO_DATE('2026-04-02', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp14, v_rh4, TO_DATE('2026-04-06', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp5, v_rh5, TO_DATE('2026-03-20', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp6, v_rh5, TO_DATE('2026-03-25', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp8, v_rh5, TO_DATE('2026-03-30', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp10, v_rh5, TO_DATE('2026-04-04', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp11, v_rh5, TO_DATE('2026-04-08', 'YYYY-MM-DD')
    );

    INSERT INTO REC_APLICA_MED_PROT (
        cod_medida_proteccao, codigo_rh, data_impl
    ) VALUES (
        v_mp15, v_rh5, TO_DATE('2026-04-12', 'YYYY-MM-DD')
    );

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh1, v_par1, c_eqp2, 7.2, TO_DATE('2026-01-15', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq1;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh1, v_par2, c_eqp5, 1.8, TO_DATE('2026-01-15', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq2;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh1, v_par3, c_eqp6, 24.1, TO_DATE('2026-01-15', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq3;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh1, v_par4, c_eqp7, 0.7, TO_DATE('2026-01-15', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq4;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh1, v_par5, c_eqp8, 6.8, TO_DATE('2026-01-15', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq5;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh2, v_par1, c_eqp2, 7.5, TO_DATE('2026-01-16', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq6;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh2, v_par2, c_eqp5, 2.5, TO_DATE('2026-01-16', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq7;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh2, v_par3, c_eqp6, 25.3, TO_DATE('2026-01-16', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq8;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh2, v_par4, c_eqp7, 0.5, TO_DATE('2026-01-16', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq9;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh2, v_par5, c_eqp8, 6.2, TO_DATE('2026-01-16', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq10;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh3, v_par1, c_eqp2, 6.9, TO_DATE('2026-01-17', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq11;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh3, v_par2, c_eqp5, 3.1, TO_DATE('2026-01-17', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq12;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh3, v_par3, c_eqp6, 24.8, TO_DATE('2026-01-17', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq13;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh3, v_par4, c_eqp7, 0.6, TO_DATE('2026-01-17', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq14;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh3, v_par5, c_eqp8, 5.9, TO_DATE('2026-01-17', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq15;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh4, v_par1, c_eqp2, 7.1, TO_DATE('2026-01-18', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq16;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh4, v_par2, c_eqp5, 1.2, TO_DATE('2026-01-18', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq17;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh4, v_par3, c_eqp6, 23.7, TO_DATE('2026-01-18', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq18;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh4, v_par4, c_eqp7, 0.8, TO_DATE('2026-01-18', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq19;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh4, v_par5, c_eqp8, 6.5, TO_DATE('2026-01-18', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq20;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh5, v_par1, c_eqp2, 7.0, TO_DATE('2026-01-19', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq21;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh5, v_par2, c_eqp5, 2.0, TO_DATE('2026-01-19', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq22;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh5, v_par3, c_eqp6, 25.0, TO_DATE('2026-01-19', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq23;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh5, v_par4, c_eqp7, 0.4, TO_DATE('2026-01-19', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq24;

    INSERT INTO Medicao_Qualidade_Agua (
        cod_qualidade_agua, codigo_rh, codigo_parametro, equipe_id, valor, data_medicao
    ) VALUES (
        seq_medicao_qualidade_agua.NEXTVAL, v_rh5, v_par5, c_eqp8, 6.5, TO_DATE('2026-01-19', 'YYYY-MM-DD')
    ) RETURNING cod_qualidade_agua INTO v_mq25;

    INSERT INTO Transferencia_Cota (
        codigo_tc, cod_fam_doadora_tc, cod_fam_receptora_tc, volume_cedido_tc, motivo_solicitacao_tc, data_aprovacao_tc, validade_transferencia_tc
    ) VALUES (
        seq_transferencia_cota.NEXTVAL, c_fb1, c_fb2, 40.00, 'Necessidade emergencial', TO_DATE('2026-04-01', 'YYYY-MM-DD'), TO_DATE('2026-04-30', 'YYYY-MM-DD')
    ) RETURNING codigo_tc INTO v_tc1;

    INSERT INTO Transferencia_Cota (
        codigo_tc, cod_fam_doadora_tc, cod_fam_receptora_tc, volume_cedido_tc, motivo_solicitacao_tc, data_aprovacao_tc, validade_transferencia_tc
    ) VALUES (
        seq_transferencia_cota.NEXTVAL, c_fb3, c_fb4, 25.00, 'Apoio comunitario', TO_DATE('2026-04-05', 'YYYY-MM-DD'), TO_DATE('2026-04-25', 'YYYY-MM-DD')
    ) RETURNING codigo_tc INTO v_tc2;

    INSERT INTO Transferencia_Cota (
        codigo_tc, cod_fam_doadora_tc, cod_fam_receptora_tc, volume_cedido_tc, motivo_solicitacao_tc, data_aprovacao_tc, validade_transferencia_tc
    ) VALUES (
        seq_transferencia_cota.NEXTVAL, c_fb2, c_fb5, 35.00, 'Reajuste de consumo', TO_DATE('2026-04-10', 'YYYY-MM-DD'), TO_DATE('2026-05-10', 'YYYY-MM-DD')
    ) RETURNING codigo_tc INTO v_tc3;

    INSERT INTO Transferencia_Cota (
        codigo_tc, cod_fam_doadora_tc, cod_fam_receptora_tc, volume_cedido_tc, motivo_solicitacao_tc, data_aprovacao_tc, validade_transferencia_tc
    ) VALUES (
        seq_transferencia_cota.NEXTVAL, c_fb4, c_fb1, 20.00, 'Troca temporaria', TO_DATE('2026-04-12', 'YYYY-MM-DD'), TO_DATE('2026-05-12', 'YYYY-MM-DD')
    ) RETURNING codigo_tc INTO v_tc4;

    INSERT INTO Transferencia_Cota (
        codigo_tc, cod_fam_doadora_tc, cod_fam_receptora_tc, volume_cedido_tc, motivo_solicitacao_tc, data_aprovacao_tc, validade_transferencia_tc
    ) VALUES (
        seq_transferencia_cota.NEXTVAL, c_fb5, c_fb3, 15.00, 'Excedente de cota', TO_DATE('2026-04-15', 'YYYY-MM-DD'), TO_DATE('2026-05-15', 'YYYY-MM-DD')
    ) RETURNING codigo_tc INTO v_tc5;

    INSERT INTO FAMILIA_DOA_REC_TRANSFER (
        codigo_tc, codigo_fb, tipo_participacao
    ) VALUES (
        v_tc1, c_fb1, 'Doa'
    );

    INSERT INTO FAMILIA_DOA_REC_TRANSFER (
        codigo_tc, codigo_fb, tipo_participacao
    ) VALUES (
        v_tc1, c_fb2, 'Recebe'
    );

    INSERT INTO FAMILIA_DOA_REC_TRANSFER (
        codigo_tc, codigo_fb, tipo_participacao
    ) VALUES (
        v_tc2, c_fb3, 'Doa'
    );

    INSERT INTO FAMILIA_DOA_REC_TRANSFER (
        codigo_tc, codigo_fb, tipo_participacao
    ) VALUES (
        v_tc2, c_fb4, 'Recebe'
    );

    INSERT INTO FAMILIA_DOA_REC_TRANSFER (
        codigo_tc, codigo_fb, tipo_participacao
    ) VALUES (
        v_tc3, c_fb2, 'Doa'
    );

    INSERT INTO FAMILIA_DOA_REC_TRANSFER (
        codigo_tc, codigo_fb, tipo_participacao
    ) VALUES (
        v_tc3, c_fb5, 'Recebe'
    );

    INSERT INTO FAMILIA_DOA_REC_TRANSFER (
        codigo_tc, codigo_fb, tipo_participacao
    ) VALUES (
        v_tc4, c_fb4, 'Doa'
    );

    INSERT INTO FAMILIA_DOA_REC_TRANSFER (
        codigo_tc, codigo_fb, tipo_participacao
    ) VALUES (
        v_tc4, c_fb1, 'Recebe'
    );

    INSERT INTO FAMILIA_DOA_REC_TRANSFER (
        codigo_tc, codigo_fb, tipo_participacao
    ) VALUES (
        v_tc5, c_fb5, 'Doa'
    );

    INSERT INTO FAMILIA_DOA_REC_TRANSFER (
        codigo_tc, codigo_fb, tipo_participacao
    ) VALUES (
        v_tc5, c_fb3, 'Recebe'
    );

    INSERT INTO TRANSFER_ASSOC_COTA (
        codigo_tc, codigo_cota, codigo_fb
    ) VALUES (
        v_tc1, c_ca1, c_fb1
    );

    INSERT INTO TRANSFER_ASSOC_COTA (
        codigo_tc, codigo_cota, codigo_fb
    ) VALUES (
        v_tc1, c_ca2, c_fb2
    );

    INSERT INTO TRANSFER_ASSOC_COTA (
        codigo_tc, codigo_cota, codigo_fb
    ) VALUES (
        v_tc2, c_ca3, c_fb3
    );

    INSERT INTO TRANSFER_ASSOC_COTA (
        codigo_tc, codigo_cota, codigo_fb
    ) VALUES (
        v_tc2, c_ca4, c_fb4
    );

    INSERT INTO TRANSFER_ASSOC_COTA (
        codigo_tc, codigo_cota, codigo_fb
    ) VALUES (
        v_tc3, c_ca2, c_fb2
    );

    INSERT INTO TRANSFER_ASSOC_COTA (
        codigo_tc, codigo_cota, codigo_fb
    ) VALUES (
        v_tc3, c_ca5, c_fb5
    );

    INSERT INTO TRANSFER_ASSOC_COTA (
        codigo_tc, codigo_cota, codigo_fb
    ) VALUES (
        v_tc4, c_ca4, c_fb4
    );

    INSERT INTO TRANSFER_ASSOC_COTA (
        codigo_tc, codigo_cota, codigo_fb
    ) VALUES (
        v_tc4, c_ca1, c_fb1
    );

    INSERT INTO TRANSFER_ASSOC_COTA (
        codigo_tc, codigo_cota, codigo_fb
    ) VALUES (
        v_tc5, c_ca5, c_fb5
    );

    INSERT INTO TRANSFER_ASSOC_COTA (
        codigo_tc, codigo_cota, codigo_fb
    ) VALUES (
        v_tc5, c_ca3, c_fb3
    );

    COMMIT;
END;
/
