const EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PHONE = /^[0-9+\- ]{7,20}$/;
const POSTAL = /^[A-Za-z0-9 -]{3,10}$/;

export const hasErrors = (errors) => Object.keys(errors).length > 0;

export function validateLogin({ email, password }) {
  const e = {};
  if (!email?.trim()) e.email = 'Email is required';
  else if (!EMAIL.test(email.trim())) e.email = 'Enter a valid email';
  if (!password) e.password = 'Password is required';
  return e;
}

export function validateRegister({ name, email, password, confirmPassword, phone }) {
  const e = {};
  if (!name?.trim() || name.trim().length < 2) e.name = 'Name must be at least 2 characters';
  if (!email?.trim()) e.email = 'Email is required';
  else if (!EMAIL.test(email.trim())) e.email = 'Enter a valid email';
  if (!password || password.length < 8) e.password = 'Password must be at least 8 characters';
  if (password !== confirmPassword) e.confirmPassword = 'Passwords do not match';
  if (phone && !PHONE.test(phone)) e.phone = 'Enter a valid phone number';
  return e;
}

export function validateAddress(a) {
  const e = {};
  if (!a.name?.trim()) e.name = 'Name is required';
  if (!PHONE.test(a.phone || '')) e.phone = 'Enter a valid phone number';
  if (!a.addressLine1?.trim()) e.addressLine1 = 'Address is required';
  if (!a.city?.trim()) e.city = 'City is required';
  if (!a.state?.trim()) e.state = 'State is required';
  if (!POSTAL.test(a.postalCode || '')) e.postalCode = 'Enter a valid postal code';
  if (!a.country?.trim()) e.country = 'Country is required';
  return e;
}

export function validateCard({ cardNumber, cardHolder, expiry, cvv }) {
  const e = {};
  const digits = (cardNumber || '').replace(/\s/g, '');
  if (!/^[0-9]{12,19}$/.test(digits)) e.cardNumber = 'Card number must be 12-19 digits';
  if (!cardHolder?.trim()) e.cardHolder = 'Name on card is required';

  const match = /^(0[1-9]|1[0-2])\/(\d{2})$/.exec(expiry || '');
  if (!match) {
    e.expiry = 'Use MM/YY';
  } else {
    const month = Number(match[1]);
    const year = 2000 + Number(match[2]);
    const now = new Date();
    if (year < now.getFullYear() || (year === now.getFullYear() && month < now.getMonth() + 1)) {
      e.expiry = 'Card has expired';
    }
  }
  if (!/^[0-9]{3,4}$/.test(cvv || '')) e.cvv = 'CVV must be 3-4 digits';
  return e;
}

export function validateProduct(p) {
  const e = {};
  const price = Number(p.price);
  if (!p.name?.trim()) e.name = 'Name is required';
  if (!p.sku?.trim()) e.sku = 'SKU is required';
  if (!p.categoryId) e.categoryId = 'Choose a category';
  if (!(price > 0)) e.price = 'Price must be greater than 0';
  if (p.discountPrice !== '' && p.discountPrice != null) {
    const discount = Number(p.discountPrice);
    if (!(discount > 0)) e.discountPrice = 'Discount price must be greater than 0';
    else if (discount >= price) e.discountPrice = 'Discount price must be lower than price';
  }
  if (p.stock === '' || !(Number(p.stock) >= 0)) e.stock = 'Stock must be 0 or more';
  return e;
}

export function validateReview({ rating, comment }) {
  const e = {};
  if (!(rating >= 1 && rating <= 5)) e.rating = 'Choose a rating';
  if (comment && comment.length > 1000) e.comment = 'Maximum 1000 characters';
  return e;
}
