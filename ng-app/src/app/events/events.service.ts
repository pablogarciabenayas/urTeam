import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/Rx';

import { Event } from './events.model';


const BASE_URL = 'https://127.0.0.1:8443/api/events/';

@Injectable()
export class EventService {

	constructor(private http: Http) { }

	getEvents() {
		return this.http.get(BASE_URL)
			.map(response => response.json())
			.catch(error => this.handleError(error));
	}

	getEvent(id: number | string) {
		return this.http.get(BASE_URL + id)
			.map(response => response.json())
			.catch(error => this.handleError(error));
	}

	getEventMember(id: number | string) {
		return this.http.get(BASE_URL + id + '/members')
			.map(response => response.json())
			.catch(error => this.handleError(error));
	}

	createEvent(event: Event, start_date: string, end_date: string) {
		return this.http.post(BASE_URL, event)
			.map(response => response.json())
			.catch(error => this.handleError(error));
	}

	deleteEvent(id: number | string) {
		return this.http.delete(BASE_URL + id)
			.map(response => response.json())
			.catch(error => this.handleError(error));
	}

	updatedEvent(id: number | string, event: Event){
		return this.http.put(BASE_URL + id,event)
            .map(response => response.json())
            .catch(error => this.handleError(error));
	}

	followEvent(id: number | string){
		return this.http.put(BASE_URL + id, null)
            .map(response => response.json())
            .catch(error => this.handleError(error));
	}


	private handleError(error: any) {
		console.error(error);
		return Observable.throw("Server error (" + error.status + "): " + error.text())
	}
}