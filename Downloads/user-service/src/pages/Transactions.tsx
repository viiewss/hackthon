import React from 'react';

const Transactions: React.FC = () => {
  return (
    <div className="space-y-6">
      <div className="bg-white shadow overflow-hidden sm:rounded-md">
        <div className="px-4 py-5 sm:px-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">Transaction History</h3>
          <p className="mt-1 max-w-2xl text-sm text-gray-500">
            View and manage your transaction history.
          </p>
        </div>
        <div className="border-t border-gray-200">
          <div className="px-4 py-5 sm:p-6">
            <p className="text-gray-500">Transaction management interface coming soon...</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Transactions; 