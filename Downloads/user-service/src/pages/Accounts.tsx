import React from 'react';

const Accounts: React.FC = () => {
  return (
    <div className="space-y-6">
      <div className="bg-white shadow overflow-hidden sm:rounded-md">
        <div className="px-4 py-5 sm:px-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">Your Accounts</h3>
          <p className="mt-1 max-w-2xl text-sm text-gray-500">
            Manage your banking accounts and view balances.
          </p>
        </div>
        <div className="border-t border-gray-200">
          <div className="px-4 py-5 sm:p-6">
            <p className="text-gray-500">Account management interface coming soon...</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Accounts; 