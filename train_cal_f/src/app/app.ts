import { HttpClient } from '@angular/common/http';
import { Component, signal } from '@angular/core';

export interface Station {
  id?: string;
  name?: string;
}

@Component({
  selector: 'app-root',
  imports: [],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('Train Calculator');
  private apiUrl = 'http://localhost:8080/api/station';

  start_station = signal<string>('');
  destination_station = signal<string>('');

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.fetchTodos();
  }

  stations = signal<Station[]>([]);

  fetchTodos() {
    this.http.get<Station[]>(this.apiUrl).subscribe({
      next: (data) => this.stations.set(data),
      error: (err) => console.error('Error:', err)
    });
  }

  onStartStationChange(event: Event) {
    const element = event.target as HTMLSelectElement;
    this.start_station.set(element.value)
  }

  onDestinationStationChange(event: Event) {
    const element = event.target as HTMLSelectElement;

    this.destination_station.set(element.value)
  }

  onCalculate() {
    console.log(this.start_station());

    console.log(this.destination_station());

    console.log('calculate');
  }
}
