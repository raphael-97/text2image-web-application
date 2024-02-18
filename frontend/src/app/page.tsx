import { Button } from "@nextui-org/react";
import Link from "next/link";

export default function Home() {
  return (
    <div className="flex justify-center mt-40">
      <Link href={"/explore"}>
        <Button color="primary">Try out for free!</Button>
      </Link>
    </div>
  );
}
