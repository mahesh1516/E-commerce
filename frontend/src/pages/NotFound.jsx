import { Link } from 'react-router-dom';
import useDocumentTitle from '../hooks/useDocumentTitle';

export default function NotFound() {
  useDocumentTitle('Page not found');
  return (
    <div className="container page center">
      <h1 className="big-number">404</h1>
      <p>Sorry, we couldn't find that page.</p>
      <Link to="/" className="btn btn-primary">Back to home</Link>
    </div>
  );
}
