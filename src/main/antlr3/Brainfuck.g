grammar Brainfuck;

options {
	output=AST;
}

@lexer::header {
    package com.mgaudin.bfi;
}

@parser::header {
    package com.mgaudin.bfi;
}

program	: statement*
	;

statement
	:	OTHER!
	|	INC_DP
	|	DEC_DP
	| 	INC
	|	DEC
	| 	PRINT
	| 	READ
	| 	loop
	;
	
loop	:	LOOP_OPEN^ stmts=statement* LOOP_CLOSE!
	;

INC_DP	: '>';
DEC_DP	: '<';
INC	: '+';
DEC	: '-';
PRINT	: '.';
READ	: ',';
LOOP_OPEN 
	: '[';
LOOP_CLOSE
	: ']';
OTHER	: ~('>'|'<'|'+'|'-'|'.'|','|'['|']');