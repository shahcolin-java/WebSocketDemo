import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { WebSocketAPI } from './WebSocketAPI';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  userId: number;
  messasge: string;
  showTest: boolean = false;

  ngOnInit() {
    this.webSocketAPI._connect(this.userId);
  }

  constructor(public webSocketAPI: WebSocketAPI) {
  }

  subscribeToUser() {
    if (this.userId) {
      this.webSocketAPI._connect(this.userId);
      this.showTest = true;
    }
  }

  testMessage(){
    if(this.messasge){
      this.webSocketAPI._testMessage(this.userId, this.messasge);
    }
  }
}
