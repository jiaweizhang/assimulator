import { provideRouter, RouterConfig } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { AssemblerComponent } from './assembler/assembler.component';
import { AboutComponent } from './about/about.component';

const routes: RouterConfig = [
    { path: '', component: HomeComponent },
    { path: 'assembler', component: AssemblerComponent },
    { path: 'about', component: AboutComponent }
];

export const appRouterProviders = [
    provideRouter(routes)
];