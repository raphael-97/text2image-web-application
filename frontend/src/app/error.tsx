"use client";
import { Button } from "@nextui-org/react";
import React from "react";

export default function error({
  error,
  reset,
}: {
  error: Error;
  reset: () => void;
}) {
  return (
    <div className="flex justify-center flex-col items-center mt-32">
      <p className="text-danger text-2xl">{error.message}</p>
      <Button color="danger" className="mt-5" onClick={reset}>
        Try again
      </Button>
    </div>
  );
}
