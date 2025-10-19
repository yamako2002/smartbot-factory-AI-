import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { BotdetailComponent } from './pages/bot-detail/bot-detail';
import { authGuard } from './core/auth.guard';

export const routes: Routes = [
  // 🧭 Public routes
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // 🔍 Chatbot details
  { path: 'chatbots/:id', component: BotdetailComponent },

  // 🏠 Home (protected)
  {
    path: 'home',
    loadComponent: () =>
      import('./pages/home/home').then(m => m.HomeComponent),
    canActivate: [authGuard],
  },

  // 🤖 Recommendation Page (protected)
  {
    path: 'recommendation',
    loadComponent: () =>
      import('./pages/recommendation/recommendation').then(m => m.RecommendationPage),
    canActivate: [authGuard],
  },

  // 🚨 Catch-all — always last
  { path: '**', redirectTo: 'home' },
];
