
create database sigeapp;
use sigeapp;
create table eventos(
    id int auto_increment primary key,
    tipoEvento varchar(100) not null,
    tituloEvento varchar(100) not null,
    trilhaEvento varchar(100),
    descricaoEvento varchar(300),
    autor varchar(100) not null,
    dataEvento date
);
create table usuarios(
    id int auto_increment primary key,
    nome varchar(100) not null,
    cpf char(11) unique not null,
    telefone varchar(15),
    email varchar(100),
    endereco varchar(100),
    sexo char(1),
    dataNascimento date,
    login varchar(100),
    senha varchar(10)
);
insert into eventos (tipoEvento,tituloEvento,trilhaEvento,descricaoEvento,autor,dataEvento)
values (
'Palestra',
'fazer um sistema de gerenciamento de eventos',
'Conhecimentos Aplicados - Tecnologia da Informação',
'Esta Palestra tem como objetivo demonstrar parte dos ensinamentos adquiridos nas disciplinas de projeto de software e desenvolvimento 1.',
'Igor,Carlos,João,Fábio',
'2016/04/01'
)
insert into usuarios (nome,cpf,telefone,email,endereco,sexo,dataNascimento,login,senha)
values (
'Gaspar da Silva Rodrigues',
'98877665544',
'(51)99887766',
'gasparzinho@gmail.com',
'Rua Dois,123 - Porto Alegre',
'M',
'1999/09/09',
'gasparzinho',
'111111'
)