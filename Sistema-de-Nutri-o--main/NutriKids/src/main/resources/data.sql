-- Inserção de Cardápios da Semana com Datas do Calendário (21/09 a 25/09)
INSERT INTO tb_cardapios (data, dia_da_semana, prato_principal, guarnicao, sobremesa, alergenos, prato_alternativo) 
VALUES ('2026-09-21', 'Segunda-feira', 'Strogonoff de Frango', 'Arroz branco e batata palha', 'Gelatina de Morango', 'lactose, glúten', 'Frango desfiado com azeite e arroz branco');

INSERT INTO tb_cardapios (data, dia_da_semana, prato_principal, guarnicao, sobremesa, alergenos, prato_alternativo) 
VALUES ('2026-09-22', 'Terça-feira', 'Iscas de Carne Grelhada', 'Arroz, feijão carioca e salada verde', 'Maçã', 'nenhum', NULL);

INSERT INTO tb_cardapios (data, dia_da_semana, prato_principal, guarnicao, sobremesa, alergenos, prato_alternativo) 
VALUES ('2026-09-23', 'Quarta-feira', 'Macarronada à Bolonhesa', 'Queijo ralado e salada de tomate', 'Bolo de Chocolate', 'glúten, lactose, ovos', 'Macarrão de milho/arroz sem glúten e Maçã fresca');

INSERT INTO tb_cardapios (data, dia_da_semana, prato_principal, guarnicao, sobremesa, alergenos, prato_alternativo) 
VALUES ('2026-09-24', 'Quinta-feira', 'Filé de Peixe Empanado', 'Purê de batata e legumes no vapor', 'Laranja fatiada', 'glúten, peixe, lactose', 'Filé de Frango Grelhado com Arroz');

INSERT INTO tb_cardapios (data, dia_da_semana, prato_principal, guarnicao, sobremesa, alergenos, prato_alternativo) 
VALUES ('2026-09-25', 'Sexta-feira', 'Feijoada Escolar Saudável', 'Arroz, couve refogada e farofa', 'Melancia', 'glúten', 'Feijoada sem farofa (com couve e arroz)');

-- Inserção de Alunos de Exemplo (Turmas 1A, 1B e 2A)
INSERT INTO tb_alunos (nome, turma, restricao) VALUES ('Lucas Oliveira', '1A', 'Intolerância a lactose');
INSERT INTO tb_alunos (nome, turma, restricao) VALUES ('Beatriz Souza', '1A', 'Doença celíaca (alergia a glúten)');
INSERT INTO tb_alunos (nome, turma, restricao) VALUES ('Gabriel Lima', '1A', NULL);
INSERT INTO tb_alunos (nome, turma, restricao) VALUES ('Sophia Ferreira', '1A', 'Alergia severa a amendoim');

INSERT INTO tb_alunos (nome, turma, restricao) VALUES ('Enzo Gabriel', '1B', NULL);
INSERT INTO tb_alunos (nome, turma, restricao) VALUES ('Mariana Costa', '1B', 'Intolerância a lactose');
INSERT INTO tb_alunos (nome, turma, restricao) VALUES ('Pedro Henrique', '1B', 'Alergia a peixes e frutos do mar');

INSERT INTO tb_alunos (nome, turma, restricao) VALUES ('Isabella Ribeiro', '2A', NULL);
INSERT INTO tb_alunos (nome, turma, restricao) VALUES ('Thiago Martins', '2A', 'Alergia a ovo e glúten');

-- Inserção de Usuário Administrador Padrão
INSERT INTO tb_usuarios (username, senha, nome, cargo) 
VALUES ('admin', 'admin123', 'Vinicius Sirino', 'Responsável pelo Sistema');

INSERT INTO tb_usuarios(username,senha,nome,cargo)
VALUES ('yasmin.pessoa', '0308', 'Yasmin Magalhaes Pessoa', 'Estágiaria de Nutrição');