import { Routes } from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {SubscribedComponent} from "./subscribed/subscribed.component";
import {UnsubscribedComponent} from "./unsubscribed/unsubscribed.component";

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'subscribed', component: SubscribedComponent},
  { path: 'unsubscribed', component: UnsubscribedComponent},
  { path: '**', redirectTo: '' }
];
