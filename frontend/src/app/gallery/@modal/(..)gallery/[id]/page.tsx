import { Modal, ModalBody, ModalContent } from "@nextui-org/react";
import Image from "next/image";
import React from "react";

export default function page({ params }: { params: { id: string } }) {
  return (
    <Modal
      defaultOpen
      backdrop="opaque"
      hideCloseButton
      className="bg-inherit shadow-none"
      size="3xl"
    >
      <ModalContent>
        <ModalBody>
          <div className="flex justify-center">
            <Image
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
