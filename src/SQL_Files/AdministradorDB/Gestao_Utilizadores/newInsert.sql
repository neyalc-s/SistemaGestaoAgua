
SET SERVEROUTPUT OFF;
SET ECHO OFF;

INSERT INTO NO_SISTEMA (
    codigo_no, nome_no, host_no, estado_no
) VALUES (
    1, 'AdministradorDB', 'mycentos_NAT:2226', 'Activo'
);

INSERT INTO NO_SISTEMA (
    codigo_no, nome_no, host_no, estado_no
) VALUES (
    2, 'FamiliasCotasDB', 'mycentos_NAT:2222', 'Activo'
);

INSERT INTO NO_SISTEMA (
    codigo_no, nome_no, host_no, estado_no
) VALUES (
    3, 'DistribuicaoConsumoDB', 'mycentos_NAT:2223', 'Activo'
);

INSERT INTO NO_SISTEMA (
    codigo_no, nome_no, host_no, estado_no
) VALUES (
    4, 'TransferenciasRecursosDB', 'mycentos_NAT:2224', 'Activo'
);

INSERT INTO NO_SISTEMA (
    codigo_no, nome_no, host_no, estado_no
) VALUES (
    5, 'EquipesGestaoDB', 'mycentos_NAT:2225', 'Activo'
);

INSERT INTO FUNCIONARIO (
    cod_funcionario, codigo_no, nome_funcionario, username_oracle, estado_funcionario
) VALUES (
    1, 1, 'Administrador Funcionario 01', 'ADM_FUNC_01', 'Activo'
);

INSERT INTO FUNCIONARIO (
    cod_funcionario, codigo_no, nome_funcionario, username_oracle, estado_funcionario
) VALUES (
    2, 1, 'Administrador Funcionario 02', 'ADM_FUNC_02', 'Activo'
);

INSERT INTO FUNCIONARIO (
    cod_funcionario, codigo_no, nome_funcionario, username_oracle, estado_funcionario
) VALUES (
    3, 2, 'Familias Cotas Funcionario 01', 'FAM_FUNC_01', 'Activo'
);

INSERT INTO FUNCIONARIO (
    cod_funcionario, codigo_no, nome_funcionario, username_oracle, estado_funcionario
) VALUES (
    4, 2, 'Familias Cotas Funcionario 02', 'FAM_FUNC_02', 'Activo'
);

INSERT INTO FUNCIONARIO (
    cod_funcionario, codigo_no, nome_funcionario, username_oracle, estado_funcionario
) VALUES (
    5, 3, 'Distribuicao Consumo Funcionario 01', 'DIST_FUNC_01', 'Activo'
);

INSERT INTO FUNCIONARIO (
    cod_funcionario, codigo_no, nome_funcionario, username_oracle, estado_funcionario
) VALUES (
    6, 3, 'Distribuicao Consumo Funcionario 02', 'DIST_FUNC_02', 'Activo'
);

INSERT INTO FUNCIONARIO (
    cod_funcionario, codigo_no, nome_funcionario, username_oracle, estado_funcionario
) VALUES (
    7, 4, 'Transferencias Recursos Funcionario 01', 'TRANS_FUNC_01', 'Activo'
);

INSERT INTO FUNCIONARIO (
    cod_funcionario, codigo_no, nome_funcionario, username_oracle, estado_funcionario
) VALUES (
    8, 4, 'Transferencias Recursos Funcionario 02', 'TRANS_FUNC_02', 'Activo'
);

INSERT INTO FUNCIONARIO (
    cod_funcionario, codigo_no, nome_funcionario, username_oracle, estado_funcionario
) VALUES (
    9, 5, 'Equipes Gestao Funcionario 01', 'EQ_FUNC_01', 'Activo'
);

INSERT INTO FUNCIONARIO (
    cod_funcionario, codigo_no, nome_funcionario, username_oracle, estado_funcionario
) VALUES (
    10, 5, 'Equipes Gestao Funcionario 02', 'EQ_FUNC_02', 'Activo'
);

COMMIT;
