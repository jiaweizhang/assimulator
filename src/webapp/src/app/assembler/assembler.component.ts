import { Component, OnInit } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Headers, RequestOptions } from '@angular/http';

import {Observable} from 'rxjs/Rx';

@Component({
	moduleId: module.id,
	selector: 'app-assembler',
	templateUrl: 'assembler.component.html',
	styleUrls: ['assembler.component.css']
})
export class AssemblerComponent implements OnInit {
	binary: string;

	constructor(private http?:Http) {
		this.binary = '0000';
	}

	ngOnInit() {
	}

	assemble(instructions, instructionGrammars, macroGrammars) {
		var instructionsObj = instructions.value.split("\n");
		var instructionGrammarsObj = JSON.parse(instructionGrammars.value);
		var macroGrammarsObj = JSON.parse(macroGrammars.value);

		var requestObj = {
			instructions: instructionsObj,
			instructionGrammars: instructionGrammarsObj,
			macroGrammars: macroGrammarsObj
		};

		console.log(requestObj);

		let body = JSON.stringify(requestObj);
		let headers = new Headers({'Content-Type': 'application/json'});
		let options = new RequestOptions({headers: headers});
		let url = "http://localhost:8080/api/v3/assemble";

		return this.http.post(url, body, options)
			.toPromise()
			.then(this.extractData)
			.catch(this.handleError);
	}

	private extractData(res:Response) {
		let body = res.json();
		console.log(body);
		this.binary = body.data.binary;
		return body.data || {};
	}

	private handleError (error: any) {
		let errMsg = (error.message) ? error.message :
			error.status ? `${error.status} - ${error.statusText}` : 'Server error';
		console.error(errMsg); // log to console instead
		return Promise.reject(errMsg);
	}
}
