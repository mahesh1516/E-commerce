export default function LoadingSpinner({ text = 'Loading...' }) {
  return (
    <div className="spinner-wrap" role="status">
      <div className="spinner" />
      <p>{text}</p>
    </div>
  );
}
