export const ORDER_STATUSES = ['PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

/** Mirrors OrderStatus.allowedNext() in the backend. */
export const NEXT_STATUSES = {
  PENDING: ['CONFIRMED', 'CANCELLED'],
  CONFIRMED: ['PROCESSING', 'CANCELLED'],
  PROCESSING: ['SHIPPED', 'CANCELLED'],
  SHIPPED: ['DELIVERED'],
  DELIVERED: [],
  CANCELLED: [],
};

export const SORT_OPTIONS = [
  { value: 'createdAt,desc', label: 'Newest first' },
  { value: 'price,asc', label: 'Price: low to high' },
  { value: 'price,desc', label: 'Price: high to low' },
  { value: 'rating,desc', label: 'Top rated' },
  { value: 'name,asc', label: 'Name: A to Z' },
];

export const PAGE_SIZE = 12;

/** SIMULATION: card numbers ending in 0000 are declined by the backend. */
export const TEST_CARD_SUCCESS = '4111 1111 1111 1111';
export const TEST_CARD_DECLINE = '4111 1111 1111 0000';
