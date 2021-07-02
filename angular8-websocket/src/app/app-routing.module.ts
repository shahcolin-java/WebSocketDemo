import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { WebSocketAPI } from './WebSocketAPI';

const routes: Routes = [];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [WebSocketAPI]
})
export class AppRoutingModule { }
