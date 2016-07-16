/* tslint:disable:no-unused-variable */

import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';

import { AssemblerComponent } from './assembler.component';

describe('Component: Assembler', () => {
  it('should create an instance', () => {
    let component = new AssemblerComponent();
    expect(component).toBeTruthy();
  });
});
