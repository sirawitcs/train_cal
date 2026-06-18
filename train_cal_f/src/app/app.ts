import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, signal } from '@angular/core';

export interface Station {
  id?: string;
  name?: string;
}

export interface PathResult {
  path: string[];
  totalStations: number;
  changes: { at: string; to: string; toLine: string }[];
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
  stations = signal<Station[]>([]);
  pathResult = signal<PathResult | null>(null);

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.fetchStation();
  }

  fetchStation() {
    this.http.get<Station[]>(this.apiUrl).subscribe({
      next: (data) => this.stations.set(data),
      error: (err) => console.error('Error:', err)
    });
  }

  calStation(start: string, destination: string) {
    const params = new HttpParams()
      .set('start', start)
      .set('destination', destination);
    this.http.post<PathResult>(`${this.apiUrl}/v4/path`, null, { params }).subscribe({
      next: (data) => this.pathResult.set(data),
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
    this.calStation(this.start_station(), this.destination_station());
  }
}
