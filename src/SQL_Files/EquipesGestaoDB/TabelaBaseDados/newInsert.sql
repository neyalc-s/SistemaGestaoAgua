SET ECHO OFF;

SET SERVEROUTPUT OFF;

DECLARE
    c_par1 CONSTANT NUMBER := 900;
    c_par2 CONSTANT NUMBER := 901;
    c_par3 CONSTANT NUMBER := 902;
    c_par4 CONSTANT NUMBER := 903;
    c_par5 CONSTANT NUMBER := 904;
    v_com1 NUMBER;
    v_com2 NUMBER;
    v_com3 NUMBER;
    v_com4 NUMBER;
    v_com5 NUMBER;
    v_eqp1 NUMBER;
    v_eqp2 NUMBER;
    v_eqp3 NUMBER;
    v_eqp4 NUMBER;
    v_eqp5 NUMBER;
    v_eqp6 NUMBER;
    v_eqp7 NUMBER;
    v_eqp8 NUMBER;
    v_eqp9 NUMBER;
    v_eqp10 NUMBER;
    v_fm1 NUMBER;
    v_fm2 NUMBER;
    v_fm3 NUMBER;
    v_fm4 NUMBER;
    v_fm5 NUMBER;
    v_fm6 NUMBER;
    v_fm7 NUMBER;
    v_ea1 NUMBER;
    v_ea2 NUMBER;
    v_ea3 NUMBER;
    v_ea4 NUMBER;
    v_ea5 NUMBER;
    v_me1 NUMBER;
    v_me2 NUMBER;
    v_me3 NUMBER;
    v_me4 NUMBER;
    v_me5 NUMBER;
BEGIN
    INSERT INTO Comite (
        cod_comite_responsavel, nome_comite, data_criacao
    ) VALUES (
        seq_comite.NEXTVAL, 'Comite Nhamatanda', TO_DATE('2020-03-01', 'YYYY-MM-DD')
    ) RETURNING cod_comite_responsavel INTO v_com1;

    INSERT INTO Comite (
        cod_comite_responsavel, nome_comite, data_criacao
    ) VALUES (
        seq_comite.NEXTVAL, 'Comite Gorongoza', TO_DATE('2021-06-15', 'YYYY-MM-DD')
    ) RETURNING cod_comite_responsavel INTO v_com2;

    INSERT INTO Comite (
        cod_comite_responsavel, nome_comite, data_criacao
    ) VALUES (
        seq_comite.NEXTVAL, 'Comite Muanza', TO_DATE('2022-09-21', 'YYYY-MM-DD')
    ) RETURNING cod_comite_responsavel INTO v_com3;

    INSERT INTO Comite (
        cod_comite_responsavel, nome_comite, data_criacao
    ) VALUES (
        seq_comite.NEXTVAL, 'Comite Chitengo', TO_DATE('2019-12-12', 'YYYY-MM-DD')
    ) RETURNING cod_comite_responsavel INTO v_com4;

    INSERT INTO Comite (
        cod_comite_responsavel, nome_comite, data_criacao
    ) VALUES (
        seq_comite.NEXTVAL, 'Comite Metuchira', TO_DATE('2023-04-10', 'YYYY-MM-DD')
    ) RETURNING cod_comite_responsavel INTO v_com5;

    INSERT INTO Equipe_Tecnica (
        equipe_id, nome, area_actuacao, nivel_formacao, contacto, supervisor_responsavel
    ) VALUES (
        seq_equipe_tecnica.NEXTVAL, 'Equipe Alfa', 'Manutencao de bombas', 'Tecnico profissional', '850123456', 'Joao Matola'
    ) RETURNING equipe_id INTO v_eqp1;

    INSERT INTO Equipe_Tecnica (
        equipe_id, nome, area_actuacao, nivel_formacao, contacto, supervisor_responsavel
    ) VALUES (
        seq_equipe_tecnica.NEXTVAL, 'Equipe Beta', 'Analise da qualidade da agua', 'Licenciatura', '850654321', 'Maria Cuamba'
    ) RETURNING equipe_id INTO v_eqp2;

    INSERT INTO Equipe_Tecnica (
        equipe_id, nome, area_actuacao, nivel_formacao, contacto, supervisor_responsavel
    ) VALUES (
        seq_equipe_tecnica.NEXTVAL, 'Equipe Delta', 'Educacao comunitaria', 'Licenciatura', '850222333', 'Rosa Mahate'
    ) RETURNING equipe_id INTO v_eqp3;

    INSERT INTO Equipe_Tecnica (
        equipe_id, nome, area_actuacao, nivel_formacao, contacto, supervisor_responsavel
    ) VALUES (
        seq_equipe_tecnica.NEXTVAL, 'Equipe Sigma', 'Distribuicao e rede hidraulica', 'Tecnico profissional', '850999888', 'Carlos Nhabinde'
    ) RETURNING equipe_id INTO v_eqp4;

    INSERT INTO Equipe_Tecnica (
        equipe_id, nome, area_actuacao, nivel_formacao, contacto, supervisor_responsavel
    ) VALUES (
        seq_equipe_tecnica.NEXTVAL, 'Equipe Omega', 'Gestao e coordenacao', 'Outro', '850111222', 'Paulo Andre'
    ) RETURNING equipe_id INTO v_eqp5;

    INSERT INTO Equipe_Tecnica (
        equipe_id, nome, area_actuacao, nivel_formacao, contacto, supervisor_responsavel
    ) VALUES (
        seq_equipe_tecnica.NEXTVAL, 'Equipe Gama', 'Monitoramento de temperatura da agua', 'Tecnico profissional', '850333444', 'Maria Cuamba'
    ) RETURNING equipe_id INTO v_eqp6;

    INSERT INTO Equipe_Tecnica (
        equipe_id, nome, area_actuacao, nivel_formacao, contacto, supervisor_responsavel
    ) VALUES (
        seq_equipe_tecnica.NEXTVAL, 'Equipe Zeta', 'Monitoramento de cloro residual', 'Tecnico profissional', '850444555', 'Maria Cuamba'
    ) RETURNING equipe_id INTO v_eqp7;

    INSERT INTO Equipe_Tecnica (
        equipe_id, nome, area_actuacao, nivel_formacao, contacto, supervisor_responsavel
    ) VALUES (
        seq_equipe_tecnica.NEXTVAL, 'Equipe Lambda', 'Monitoramento de oxigenio dissolvido', 'Licenciatura', '850555666', 'Maria Cuamba'
    ) RETURNING equipe_id INTO v_eqp8;

    INSERT INTO Equipe_Tecnica (
        equipe_id, nome, area_actuacao, nivel_formacao, contacto, supervisor_responsavel
    ) VALUES (
        seq_equipe_tecnica.NEXTVAL, 'Equipe Kappa', 'Manutencao de tanques e reservatorios', 'Tecnico profissional', '850666777', 'Carlos Nhabinde'
    ) RETURNING equipe_id INTO v_eqp9;

    INSERT INTO Equipe_Tecnica (
        equipe_id, nome, area_actuacao, nivel_formacao, contacto, supervisor_responsavel
    ) VALUES (
        seq_equipe_tecnica.NEXTVAL, 'Equipe Teta', 'Manutencao de chafarizes e cloradores', 'Tecnico profissional', '850777888', 'Carlos Nhabinde'
    ) RETURNING equipe_id INTO v_eqp10;

    INSERT INTO Tecnico_Manutencao (
        equipe_id, habilidade_tecnica, tempo_medio_resposta
    ) VALUES (
        v_eqp1, 'Reparo hidraulico em bombas e valvulas', 24
    );

    INSERT INTO Tecnico_Manutencao (
        equipe_id, habilidade_tecnica, tempo_medio_resposta
    ) VALUES (
        v_eqp4, 'Manutencao de redes de distribuicao e tubos', 18
    );

    INSERT INTO Tecnico_Manutencao (
        equipe_id, habilidade_tecnica, tempo_medio_resposta
    ) VALUES (
        v_eqp9, 'Manutencao preventiva de tanques e reservatorios', 20
    );

    INSERT INTO Tecnico_Manutencao (
        equipe_id, habilidade_tecnica, tempo_medio_resposta
    ) VALUES (
        v_eqp10, 'Manutencao de chafarizes, cloradores e torneiras', 16
    );

    INSERT INTO Analista_Qualidade (
        equipe_id, codigo_parametro, especialidade_analise, frequencia_amostragem
    ) VALUES (
        v_eqp2, c_par1, 'Analise de pH da agua', 'Semanal'
    );

    INSERT INTO Analista_Qualidade (
        equipe_id, codigo_parametro, especialidade_analise, frequencia_amostragem
    ) VALUES (
        v_eqp5, c_par2, 'Analise de turbidez da agua', 'Quinzenal'
    );

    INSERT INTO Analista_Qualidade (
        equipe_id, codigo_parametro, especialidade_analise, frequencia_amostragem
    ) VALUES (
        v_eqp6, c_par3, 'Analise de temperatura da agua', 'Semanal'
    );

    INSERT INTO Analista_Qualidade (
        equipe_id, codigo_parametro, especialidade_analise, frequencia_amostragem
    ) VALUES (
        v_eqp7, c_par4, 'Analise de cloro residual', 'Semanal'
    );

    INSERT INTO Analista_Qualidade (
        equipe_id, codigo_parametro, especialidade_analise, frequencia_amostragem
    ) VALUES (
        v_eqp8, c_par5, 'Analise de oxigenio dissolvido', 'Mensal'
    );

    INSERT INTO Educador_Comunitario (
        equipe_id, metodologia_sensibilizacao, lingua_local, comunidade_atendida
    ) VALUES (
        v_eqp3, 'Oficinas praticas, teatro, radio e visitas domiciliares', 'Portugues, Sena, Ndau', 'Nhamatanda, Muanza, Gorongoza'
    );

    INSERT INTO Ferramenta_Manutencao (
        cod_ferramenta_disponivel, equipe_id, nome_ferramenta
    ) VALUES (
        seq_ferramenta_manutencao.NEXTVAL, v_eqp1, 'Chave Inglesa 12"'
    ) RETURNING cod_ferramenta_disponivel INTO v_fm1;

    INSERT INTO Ferramenta_Manutencao (
        cod_ferramenta_disponivel, equipe_id, nome_ferramenta
    ) VALUES (
        seq_ferramenta_manutencao.NEXTVAL, v_eqp1, 'Bomba de teste manual'
    ) RETURNING cod_ferramenta_disponivel INTO v_fm2;

    INSERT INTO Ferramenta_Manutencao (
        cod_ferramenta_disponivel, equipe_id, nome_ferramenta
    ) VALUES (
        seq_ferramenta_manutencao.NEXTVAL, v_eqp4, 'Multimetro digital'
    ) RETURNING cod_ferramenta_disponivel INTO v_fm3;

    INSERT INTO Ferramenta_Manutencao (
        cod_ferramenta_disponivel, equipe_id, nome_ferramenta
    ) VALUES (
        seq_ferramenta_manutencao.NEXTVAL, v_eqp4, 'Alicate de pressao'
    ) RETURNING cod_ferramenta_disponivel INTO v_fm4;

    INSERT INTO Ferramenta_Manutencao (
        cod_ferramenta_disponivel, equipe_id, nome_ferramenta
    ) VALUES (
        seq_ferramenta_manutencao.NEXTVAL, v_eqp4, 'Chave de fenda isolada'
    ) RETURNING cod_ferramenta_disponivel INTO v_fm5;

    INSERT INTO Ferramenta_Manutencao (
        cod_ferramenta_disponivel, equipe_id, nome_ferramenta
    ) VALUES (
        seq_ferramenta_manutencao.NEXTVAL, v_eqp9, 'Kit de vedacao para reservatorio'
    ) RETURNING cod_ferramenta_disponivel INTO v_fm6;

    INSERT INTO Ferramenta_Manutencao (
        cod_ferramenta_disponivel, equipe_id, nome_ferramenta
    ) VALUES (
        seq_ferramenta_manutencao.NEXTVAL, v_eqp10, 'Kit de manutencao de clorador'
    ) RETURNING cod_ferramenta_disponivel INTO v_fm7;

    INSERT INTO Equipamento_Analista (
        cod_equipamento, equipe_id, nome_equipamento
    ) VALUES (
        seq_equipamento_analista.NEXTVAL, v_eqp2, 'Medidor de pH'
    ) RETURNING cod_equipamento INTO v_ea1;

    INSERT INTO Equipamento_Analista (
        cod_equipamento, equipe_id, nome_equipamento
    ) VALUES (
        seq_equipamento_analista.NEXTVAL, v_eqp5, 'Condutivimetro'
    ) RETURNING cod_equipamento INTO v_ea2;

    INSERT INTO Equipamento_Analista (
        cod_equipamento, equipe_id, nome_equipamento
    ) VALUES (
        seq_equipamento_analista.NEXTVAL, v_eqp7, 'Kit de Cloro'
    ) RETURNING cod_equipamento INTO v_ea3;

    INSERT INTO Equipamento_Analista (
        cod_equipamento, equipe_id, nome_equipamento
    ) VALUES (
        seq_equipamento_analista.NEXTVAL, v_eqp8, 'Espectrofotometro'
    ) RETURNING cod_equipamento INTO v_ea4;

    INSERT INTO Equipamento_Analista (
        cod_equipamento, equipe_id, nome_equipamento
    ) VALUES (
        seq_equipamento_analista.NEXTVAL, v_eqp6, 'Termometro digital'
    ) RETURNING cod_equipamento INTO v_ea5;

    INSERT INTO Material_Educador (
        cod_material, equipe_id, nome_material
    ) VALUES (
        seq_material_educador.NEXTVAL, v_eqp3, 'Cartilha: "Uso Racional da agua"'
    ) RETURNING cod_material INTO v_me1;

    INSERT INTO Material_Educador (
        cod_material, equipe_id, nome_material
    ) VALUES (
        seq_material_educador.NEXTVAL, v_eqp3, 'Poster: "Higiene e agua Segura"'
    ) RETURNING cod_material INTO v_me2;

    INSERT INTO Material_Educador (
        cod_material, equipe_id, nome_material
    ) VALUES (
        seq_material_educador.NEXTVAL, v_eqp3, 'Manual do Educador Comunitario'
    ) RETURNING cod_material INTO v_me3;

    INSERT INTO Material_Educador (
        cod_material, equipe_id, nome_material
    ) VALUES (
        seq_material_educador.NEXTVAL, v_eqp3, 'Jogo Educativo: "Ciclo da agua"'
    ) RETURNING cod_material INTO v_me4;

    INSERT INTO Material_Educador (
        cod_material, equipe_id, nome_material
    ) VALUES (
        seq_material_educador.NEXTVAL, v_eqp3, 'Guia de Economia Domestica de agua'
    ) RETURNING cod_material INTO v_me5;

    COMMIT;
END;
/
