/**
 * Workout data model
 */
export interface Workout {
  id?: number;
  userId: number;
  name: string;
  description?: string;
  durationMinutes?: number;
  exerciseCount?: number;
  createdAt?: Date;
  updatedAt?: Date;
}
