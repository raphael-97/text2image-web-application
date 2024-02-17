import { CheckIfUserHasAccessToImage } from "@/app/lib/imageAuthActions";
import { Modal, ModalBody, ModalContent } from "@nextui-org/react";
import Image from "next/image";
import React from "react";

export default async function InterceptingModal({
  params,
}: {
  params: { id: string };
}) {
  try {
    CheckIfUserHasAccessToImage(parseInt(params.id));
  } catch (error) {
    if (error instanceof Error) throw new Error(error.message);
  }
  return (
    <Modal
      defaultOpen
      backdrop="opaque"
      hideCloseButton
      className="bg-inherit shadow-none"
      size="lg"
      placement="top-center"
    >
      <ModalContent>
        <ModalBody>
          <div className="flex justify-center">
            <Image
              unoptimized
              src={`/api/images/${params.id}`}
              alt={`img_${params.id}`}
              width={512}
              height={512}
              className="rounded-3xl"
            ></Image>
          </div>
        </ModalBody>
      </ModalContent>
    </Modal>
  );
}
