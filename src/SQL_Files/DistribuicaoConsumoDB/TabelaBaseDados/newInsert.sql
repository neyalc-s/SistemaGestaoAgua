SET ECHO OFF;

SET SERVEROUTPUT OFF;

DECLARE
    c_com1 CONSTANT NUMBER := 300;
    c_com2 CONSTANT NUMBER := 301;
    c_com3 CONSTANT NUMBER := 302;
    c_com4 CONSTANT NUMBER := 303;
    c_com5 CONSTANT NUMBER := 304;
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
    c_rh1 CONSTANT NUMBER := 2000;
    c_rh2 CONSTANT NUMBER := 2001;
    c_rh3 CONSTANT NUMBER := 2002;
    c_rh4 CONSTANT NUMBER := 2003;
    c_rh5 CONSTANT NUMBER := 2004;
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
    v_pd1 NUMBER;
    v_pd2 NUMBER;
    v_pd3 NUMBER;
    v_pd4 NUMBER;
    v_pd5 NUMBER;
    v_hm1 NUMBER;
    v_hm2 NUMBER;
    v_hm3 NUMBER;
    v_hm4 NUMBER;
    v_hm5 NUMBER;
    v_ha1 NUMBER;
    v_ha2 NUMBER;
    v_ha3 NUMBER;
    v_ha4 NUMBER;
    v_ha5 NUMBER;
    v_ha6 NUMBER;
    v_ha7 NUMBER;
    v_ha8 NUMBER;
    v_ha9 NUMBER;
    v_ha10 NUMBER;
    v_rc1 NUMBER;
    v_rc2 NUMBER;
    v_rc3 NUMBER;
    v_rc4 NUMBER;
    v_rc5 NUMBER;
    v_rc6 NUMBER;
    v_rc7 NUMBER;
    v_rc8 NUMBER;
    v_rc9 NUMBER;
    v_rc10 NUMBER;
    v_rc11 NUMBER;
    v_rc12 NUMBER;
    v_rc13 NUMBER;
    v_rc14 NUMBER;
    v_rc15 NUMBER;
    v_rc16 NUMBER;
    v_rc17 NUMBER;
    v_rc18 NUMBER;
    v_rc19 NUMBER;
    v_rc20 NUMBER;
BEGIN
    INSERT INTO Ponto_Distribuicao (
        codigo_pd, cod_comite_responsavel, equipe_id, codigo_rh, localizacao_pd, tipo_infraestrutura_pd, capacidade_armazenamento_pd, fonte_abastecimento_pd, tecnologia_tratamento_pd, data_instalacao_pd, estado_operacional_pd, volume_actual_pd
    ) VALUES (
        seq_ponto_distribuicao.NEXTVAL, c_com1, c_eqp1, c_rh1, 'Nhamatanda Centro', 'Chafariz', 500.00, 'Poco 1801', 'Filtracao simples', TO_DATE('2022-06-01', 'YYYY-MM-DD'), 'Activo', 449.50
    ) RETURNING codigo_pd INTO v_pd1;

    INSERT INTO Ponto_Distribuicao (
        codigo_pd, cod_comite_responsavel, equipe_id, codigo_rh, localizacao_pd, tipo_infraestrutura_pd, capacidade_armazenamento_pd, fonte_abastecimento_pd, tecnologia_tratamento_pd, data_instalacao_pd, estado_operacional_pd, volume_actual_pd
    ) VALUES (
        seq_ponto_distribuicao.NEXTVAL, c_com2, c_eqp4, c_rh2, 'Gorongoza Sul', 'Reservatorio', 700.00, 'Rio 1802', 'Cloracao', TO_DATE('2022-07-15', 'YYYY-MM-DD'), 'Activo', 634.25
    ) RETURNING codigo_pd INTO v_pd2;

    INSERT INTO Ponto_Distribuicao (
        codigo_pd, cod_comite_responsavel, equipe_id, codigo_rh, localizacao_pd, tipo_infraestrutura_pd, capacidade_armazenamento_pd, fonte_abastecimento_pd, tecnologia_tratamento_pd, data_instalacao_pd, estado_operacional_pd, volume_actual_pd
    ) VALUES (
        seq_ponto_distribuicao.NEXTVAL, c_com3, c_eqp1, c_rh3, 'Muanza Norte', 'Chafariz', 450.00, 'Poco 1803', NULL, TO_DATE('2022-08-20', 'YYYY-MM-DD'), 'Activo', 414.60
    ) RETURNING codigo_pd INTO v_pd3;

    INSERT INTO Ponto_Distribuicao (
        codigo_pd, cod_comite_responsavel, equipe_id, codigo_rh, localizacao_pd, tipo_infraestrutura_pd, capacidade_armazenamento_pd, fonte_abastecimento_pd, tecnologia_tratamento_pd, data_instalacao_pd, estado_operacional_pd, volume_actual_pd
    ) VALUES (
        seq_ponto_distribuicao.NEXTVAL, c_com4, c_eqp9, c_rh5, 'Chitengo', 'Tanque', 600.00, 'Fonte 1805', 'Filtracao', TO_DATE('2023-01-01', 'YYYY-MM-DD'), 'Activo', 541.40
    ) RETURNING codigo_pd INTO v_pd4;

    INSERT INTO Ponto_Distribuicao (
        codigo_pd, cod_comite_responsavel, equipe_id, codigo_rh, localizacao_pd, tipo_infraestrutura_pd, capacidade_armazenamento_pd, fonte_abastecimento_pd, tecnologia_tratamento_pd, data_instalacao_pd, estado_operacional_pd, volume_actual_pd
    ) VALUES (
        seq_ponto_distribuicao.NEXTVAL, c_com5, c_eqp10, c_rh4, 'Metuchira', 'Chafariz', 550.00, 'Poco 1804', 'Cloracao', TO_DATE('2023-02-10', 'YYYY-MM-DD'), 'Activo', 479.1
    ) RETURNING codigo_pd INTO v_pd5;

    INSERT INTO Historico_Manutencao (
        cod_historico_manutencao, codigo_pd, equipe_id, data_manutencao, tipo_manutencao
    ) VALUES (
        seq_historico_manutencao.NEXTVAL, v_pd1, c_eqp1, TO_DATE('2024-03-01', 'YYYY-MM-DD'), 'Troca de valvula'
    ) RETURNING cod_historico_manutencao INTO v_hm1;

    INSERT INTO Historico_Manutencao (
        cod_historico_manutencao, codigo_pd, equipe_id, data_manutencao, tipo_manutencao
    ) VALUES (
        seq_historico_manutencao.NEXTVAL, v_pd2, c_eqp4, TO_DATE('2024-04-05', 'YYYY-MM-DD'), 'Limpeza de tanque'
    ) RETURNING cod_historico_manutencao INTO v_hm2;

    INSERT INTO Historico_Manutencao (
        cod_historico_manutencao, codigo_pd, equipe_id, data_manutencao, tipo_manutencao
    ) VALUES (
        seq_historico_manutencao.NEXTVAL, v_pd3, c_eqp1, TO_DATE('2024-05-10', 'YYYY-MM-DD'), 'Reparo da bomba'
    ) RETURNING cod_historico_manutencao INTO v_hm3;

    INSERT INTO Historico_Manutencao (
        cod_historico_manutencao, codigo_pd, equipe_id, data_manutencao, tipo_manutencao
    ) VALUES (
        seq_historico_manutencao.NEXTVAL, v_pd4, c_eqp9, TO_DATE('2024-06-12', 'YYYY-MM-DD'), 'Verificacao eletrica'
    ) RETURNING cod_historico_manutencao INTO v_hm4;

    INSERT INTO Historico_Manutencao (
        cod_historico_manutencao, codigo_pd, equipe_id, data_manutencao, tipo_manutencao
    ) VALUES (
        seq_historico_manutencao.NEXTVAL, v_pd5, c_eqp10, TO_DATE('2024-07-18', 'YYYY-MM-DD'), 'Substituicao de cano'
    ) RETURNING cod_historico_manutencao INTO v_hm5;

    INSERT INTO Historico_Abastecimento (
        cod_abastecimento, codigo_pd, codigo_rh, volume_abastecido, data_inicio, data_fim, duracao_horas, estado_abastecimento
    ) VALUES (
        seq_historico_abastecimento.NEXTVAL, v_pd1, c_rh1, 300.00, TO_DATE('2026-04-10 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2026-04-10 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1.00, 'Concluido'
    ) RETURNING cod_abastecimento INTO v_ha1;

    INSERT INTO Historico_Abastecimento (
        cod_abastecimento, codigo_pd, codigo_rh, volume_abastecido, data_inicio, data_fim, duracao_horas, estado_abastecimento
    ) VALUES (
        seq_historico_abastecimento.NEXTVAL, v_pd2, c_rh2, 500.00, TO_DATE('2026-04-11 07:30:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2026-04-11 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1.50, 'Concluido'
    ) RETURNING cod_abastecimento INTO v_ha2;

    INSERT INTO Historico_Abastecimento (
        cod_abastecimento, codigo_pd, codigo_rh, volume_abastecido, data_inicio, data_fim, duracao_horas, estado_abastecimento
    ) VALUES (
        seq_historico_abastecimento.NEXTVAL, v_pd3, c_rh3, 250.00, TO_DATE('2026-04-12 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2026-04-12 11:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1.00, 'Concluido'
    ) RETURNING cod_abastecimento INTO v_ha3;

    INSERT INTO Historico_Abastecimento (
        cod_abastecimento, codigo_pd, codigo_rh, volume_abastecido, data_inicio, data_fim, duracao_horas, estado_abastecimento
    ) VALUES (
        seq_historico_abastecimento.NEXTVAL, v_pd4, c_rh5, 400.00, TO_DATE('2026-04-13 06:45:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2026-04-13 08:45:00', 'YYYY-MM-DD HH24:MI:SS'), 2.00, 'Concluido'
    ) RETURNING cod_abastecimento INTO v_ha4;

    INSERT INTO Historico_Abastecimento (
        cod_abastecimento, codigo_pd, codigo_rh, volume_abastecido, data_inicio, data_fim, duracao_horas, estado_abastecimento
    ) VALUES (
        seq_historico_abastecimento.NEXTVAL, v_pd5, c_rh4, 350.00, TO_DATE('2026-04-14 09:15:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2026-04-14 10:45:00', 'YYYY-MM-DD HH24:MI:SS'), 1.50, 'Concluido'
    ) RETURNING cod_abastecimento INTO v_ha5;

    INSERT INTO Historico_Abastecimento (
        cod_abastecimento, codigo_pd, codigo_rh, volume_abastecido, data_inicio, data_fim, duracao_horas, estado_abastecimento
    ) VALUES (
        seq_historico_abastecimento.NEXTVAL, v_pd1, c_rh1, 120.00, TO_DATE('2026-04-16 07:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2026-04-16 07:30:00', 'YYYY-MM-DD HH24:MI:SS'), 0.50, 'Concluido'
    ) RETURNING cod_abastecimento INTO v_ha6;

    INSERT INTO Historico_Abastecimento (
        cod_abastecimento, codigo_pd, codigo_rh, volume_abastecido, data_inicio, data_fim, duracao_horas, estado_abastecimento
    ) VALUES (
        seq_historico_abastecimento.NEXTVAL, v_pd2, c_rh2, 180.00, TO_DATE('2026-04-17 06:30:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2026-04-17 07:15:00', 'YYYY-MM-DD HH24:MI:SS'), 0.75, 'Concluido'
    ) RETURNING cod_abastecimento INTO v_ha7;

    INSERT INTO Historico_Abastecimento (
        cod_abastecimento, codigo_pd, codigo_rh, volume_abastecido, data_inicio, data_fim, duracao_horas, estado_abastecimento
    ) VALUES (
        seq_historico_abastecimento.NEXTVAL, v_pd3, c_rh3, 90.00, TO_DATE('2026-04-18 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2026-04-18 09:20:00', 'YYYY-MM-DD HH24:MI:SS'), 0.33, 'Cancelado'
    ) RETURNING cod_abastecimento INTO v_ha8;

    INSERT INTO Historico_Abastecimento (
        cod_abastecimento, codigo_pd, codigo_rh, volume_abastecido, data_inicio, data_fim, duracao_horas, estado_abastecimento
    ) VALUES (
        seq_historico_abastecimento.NEXTVAL, v_pd4, c_rh5, 160.00, TO_DATE('2026-04-19 14:00:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2026-04-19 15:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1.00, 'Concluido'
    ) RETURNING cod_abastecimento INTO v_ha9;

    INSERT INTO Historico_Abastecimento (
        cod_abastecimento, codigo_pd, codigo_rh, volume_abastecido, data_inicio, data_fim, duracao_horas, estado_abastecimento
    ) VALUES (
        seq_historico_abastecimento.NEXTVAL, v_pd5, c_rh4, 140.00, TO_DATE('2026-04-20 11:15:00', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2026-04-20 11:45:00', 'YYYY-MM-DD HH24:MI:SS'), 0.50, 'Concluido'
    ) RETURNING cod_abastecimento INTO v_ha10;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb1, v_pd1, c_ca1, TO_TIMESTAMP('2026-04-21 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 25.00, 'Ana Joaquim', 'MANUAL', 225.00, 'Consumo diario normal'
    ) RETURNING codigo_rc INTO v_rc1;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb2, v_pd2, c_ca2, TO_TIMESTAMP('2026-04-22 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 35.00, 'Pedro Matola', 'MANUAL', 265.00, 'Uso moderado'
    ) RETURNING codigo_rc INTO v_rc2;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb3, v_pd3, c_ca3, TO_TIMESTAMP('2026-04-23 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), 15.40, 'Luisa Andre', 'MANUAL', 184.60, 'Consumo reduzido'
    ) RETURNING codigo_rc INTO v_rc3;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb4, v_pd4, c_ca4, TO_TIMESTAMP('2026-04-24 11:15:00', 'YYYY-MM-DD HH24:MI:SS'), 30.10, 'Rafael Muchele', 'MANUAL', 319.90, 'Consumo normal'
    ) RETURNING codigo_rc INTO v_rc4;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb5, v_pd5, c_ca5, TO_TIMESTAMP('2026-04-25 12:00:00', 'YYYY-MM-DD HH24:MI:SS'), 38.90, 'Marta Nunes', 'MANUAL', 111.10, 'Consumo alto'
    ) RETURNING codigo_rc INTO v_rc5;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb1, v_pd1, c_ca6, TO_TIMESTAMP('2026-04-28 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 25.50, 'Ana Joaquim', 'MANUAL', 224.50, 'Consumo normal'
    ) RETURNING codigo_rc INTO v_rc6;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb2, v_pd2, c_ca8, TO_TIMESTAMP('2026-04-28 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 30.75, 'Pedro Matola', 'MANUAL', 269.25, 'Uso moderado'
    ) RETURNING codigo_rc INTO v_rc7;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb3, v_pd3, c_ca10, TO_TIMESTAMP('2026-04-29 10:30:00', 'YYYY-MM-DD HH24:MI:SS'), 20.00, 'Luisa Andre', 'MANUAL', 180.00, 'Consumo reduzido'
    ) RETURNING codigo_rc INTO v_rc8;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb4, v_pd4, c_ca12, TO_TIMESTAMP('2026-04-30 11:15:00', 'YYYY-MM-DD HH24:MI:SS'), 28.50, 'Rafael Muchele', 'MANUAL', 321.50, 'Consumo normal'
    ) RETURNING codigo_rc INTO v_rc9;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb5, v_pd5, c_ca14, TO_TIMESTAMP('2026-05-01 12:00:00', 'YYYY-MM-DD HH24:MI:SS'), 32.00, 'Marta Nunes', 'MANUAL', 118.00, 'Consumo moderado'
    ) RETURNING codigo_rc INTO v_rc10;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb6, v_pd1, c_ca16, TO_TIMESTAMP('2026-04-22 07:45:00', 'YYYY-MM-DD HH24:MI:SS'), 70.00, 'Celina Chissano', 'MANUAL', 330.00, 'Retirada familiar em cota expirada'
    ) RETURNING codigo_rc INTO v_rc11;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb6, v_pd1, c_ca17, TO_TIMESTAMP('2026-04-29 08:10:00', 'YYYY-MM-DD HH24:MI:SS'), 55.00, 'Celina Chissano', 'MANUAL', 345.00, 'Retirada familiar em cota valida'
    ) RETURNING codigo_rc INTO v_rc12;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb7, v_pd2, c_ca19, TO_TIMESTAMP('2026-04-23 09:20:00', 'YYYY-MM-DD HH24:MI:SS'), 22.00, 'Manuel Sithole', 'MANUAL', 78.00, 'Retirada reduzida em cota expirada'
    ) RETURNING codigo_rc INTO v_rc13;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb7, v_pd2, c_ca20, TO_TIMESTAMP('2026-04-30 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 10.50, 'Manuel Sithole', 'MANUAL', 89.50, 'Retirada controlada em cota valida'
    ) RETURNING codigo_rc INTO v_rc14;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb8, v_pd3, c_ca22, TO_TIMESTAMP('2026-04-24 11:05:00', 'YYYY-MM-DD HH24:MI:SS'), 60.00, 'Esperanca Dique', 'MANUAL', 390.00, 'Retirada de familia numerosa em cota expirada'
    ) RETURNING codigo_rc INTO v_rc15;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb8, v_pd3, c_ca23, TO_TIMESTAMP('2026-04-30 13:15:00', 'YYYY-MM-DD HH24:MI:SS'), 40.00, 'Esperanca Dique', 'MANUAL', 410.00, 'Retirada normal em cota valida'
    ) RETURNING codigo_rc INTO v_rc16;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb9, v_pd4, c_ca25, TO_TIMESTAMP('2026-04-25 15:00:00', 'YYYY-MM-DD HH24:MI:SS'), 32.00, 'Antonio Bambo', 'MANUAL', 168.00, 'Retirada em cota expirada'
    ) RETURNING codigo_rc INTO v_rc17;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb9, v_pd4, c_ca26, TO_TIMESTAMP('2026-04-29 16:30:00', 'YYYY-MM-DD HH24:MI:SS'), 21.00, 'Antonio Bambo', 'MANUAL', 179.00, 'Retirada normal em cota valida'
    ) RETURNING codigo_rc INTO v_rc18;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb10, v_pd5, c_ca28, TO_TIMESTAMP('2026-04-26 08:40:00', 'YYYY-MM-DD HH24:MI:SS'), 60.00, 'Sara Macuacua', 'MANUAL', 240.00, 'Retirada de fim de semana em cota expirada'
    ) RETURNING codigo_rc INTO v_rc19;

    INSERT INTO Registro_Consumo (
        codigo_rc, codigo_fb, codigo_pd, codigo_cota, data_hora_rc, volume_retirado_rc, pessoa_coleta_rc, metodo_autenticacao_rc, saldo_cota_rc, observacao_rc
    ) VALUES (
        seq_registro_consumo.NEXTVAL, c_fb10, v_pd5, c_ca29, TO_TIMESTAMP('2026-04-30 17:10:00', 'YYYY-MM-DD HH24:MI:SS'), 32.50, 'Sara Macuacua', 'MANUAL', 267.50, 'Retirada normal em cota valida'
    ) RETURNING codigo_rc INTO v_rc20;

    COMMIT;
END;
/
