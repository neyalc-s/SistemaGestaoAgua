SET ECHO OFF;

-- Procedures expostas a outros nos para esta operacao.

CREATE OR REPLACE PROCEDURE PRC_VALIDAR_COMITE_DIST (
    p_codigo_comite IN NUMBER,
    p_existe        OUT NUMBER,
    p_mensagem      OUT VARCHAR2
)
IS
BEGIN
    p_existe := 0;
    p_mensagem := NULL;

    IF p_codigo_comite IS NULL THEN
        p_mensagem := 'Codigo do comite e obrigatorio.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO p_existe
      FROM Comite
     WHERE cod_comite_responsavel = p_codigo_comite
       AND ROWNUM = 1;

    IF p_existe = 0 THEN
        p_mensagem := 'Comite nao encontrado.';
    ELSE
        p_mensagem := 'Comite encontrado.';
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        p_existe := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/

CREATE OR REPLACE PROCEDURE PRC_VALIDAR_TECNICO_MANUT_DIST (
    p_equipe_id IN NUMBER,
    p_existe    OUT NUMBER,
    p_mensagem  OUT VARCHAR2
)
IS
BEGIN
    p_existe := 0;
    p_mensagem := NULL;

    IF p_equipe_id IS NULL THEN
        p_mensagem := 'Equipe tecnica e obrigatoria.';
        RETURN;
    END IF;

    SELECT COUNT(*)
      INTO p_existe
      FROM Tecnico_Manutencao
     WHERE equipe_id = p_equipe_id
       AND ROWNUM = 1;

    IF p_existe = 0 THEN
        p_mensagem := 'A equipe selecionada deve ser do tipo Tecnico de Manutencao.';
    ELSE
        p_mensagem := 'Tecnico de manutencao encontrado.';
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        p_existe := 0;
        p_mensagem := 'Operacao nao concluida. Detalhe: ' || SQLERRM;
END;
/
