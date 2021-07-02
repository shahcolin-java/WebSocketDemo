import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { AppComponent } from './app.component';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';


@Injectable()
export class WebSocketAPI {
    TAG = '[SOCKET SERVICE]';
    baseUrl = "http://localhost:8080";
    webSocketEndPoint: string = this.baseUrl + '/ws/connect';
    topic: string = "/topic";
    stompClient: any;

    TOTAL_COUNT_REFRESH_TICKET: number = 10;
    CHANNELS = {
        EVENT: '/user/queue/event',
        STATUS: '/user/queue/status',
        REGISTER: '/app/register',
    };
    private STATUS = {
        OK: 'ok',
        UNAUTHORIZED: 'unauthorized',
        REFRESH_CONNECTION: 'refresh_connection',
        ERROR: 'error',
    };

    countRefreshConnection: number = 0;
    listners = [];
    container = [];
    msg = [];
    connection: string = null;

    constructor(private http: HttpClient) { }

    _connect(userId) {
        console.log("Initialize WebSocket Connection");
        let ws = new SockJS(this.webSocketEndPoint);
        this.stompClient = Stomp.over(ws);
        const that = this;
        that.stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);

            if (userId > 0) {
                that.stompClient.subscribe(that.CHANNELS.STATUS, (payload, headers, res) => {
                    switch (payload.body) {
                        case that.STATUS.OK:
                            that.countRefreshConnection = 0;
                            break;
                        case that.STATUS.UNAUTHORIZED:
                            console.log(that.TAG, 'Unauthorized');
                            break;
                        case that.STATUS.REFRESH_CONNECTION:
                            that.countRefreshConnection++;
                            that.connection = null;
                            if (that.countRefreshConnection < that.TOTAL_COUNT_REFRESH_TICKET) {
                                that.registr(userId);
                            } else {
                                console.log(that.TAG, 'Refresh connection error, limit exceeded');
                            }
                            break;
                        case that.STATUS.ERROR:
                            console.error(that.TAG, new Error(res));
                            break;
                    }
                });
                that.stompClient.subscribe(that.CHANNELS.EVENT, (payload, headers, res) => {
                    that.msg.push(payload.body);
                });
                that.registr(userId);
            }
        });
    };

    sendMessageWithData(channel, data) {
        this.stompClient.send(channel, {}, JSON.stringify(data));
        return this;
    }

    getTicket(userId): Observable<object> {
        return this.http.get<object>(this.baseUrl + '/api/user/' + userId + '/connection');
    }

    registr(userId) {
        this.getTicket(userId).subscribe((data) => {
            const connection = data['connection'];
            const jsonObj = { user_id: Number(userId), connection: connection };
            return this.sendMessageWithData(this.CHANNELS.REGISTER, jsonObj);
        });
    }

    _testMessage(userId, message) {
        this.http.get<object>(this.baseUrl + '/api/test/' + userId + '/' + message)
        .subscribe(() => {
            console.log("sent message");
        });
    }
}